package com.amayr.demo.account.support;

import com.amayr.demo.account.Account;
import com.amayr.demo.account.Statistic;
import com.amayr.demo.event.Event;
import com.amayr.demo.event.support.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.amayr.demo.ControllerTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WebMvcTest(value = AccountController.class)
@Import(AccountService.class)
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountRepository accountRepository;
    @MockBean
    private EventRepository eventRepository;

    @Test
    void findAllAccounts() throws Exception {
        given(accountRepository.findAll()).willReturn(Arrays.asList(new Account("acc1"), new Account("acc2")));

        MvcResult result = this.mockMvc.perform(get("/api/account"))
                .andExpect(status().isOk())
                .andReturn();

        List accounts = parseResponseList(result, Account.class);
        assertThat(accounts).hasSize(2);
    }

    @Test
    void findAllAccountsIsEmptyIfNoneExist() throws Exception {
        given(accountRepository.findAll()).willReturn(new ArrayList<>());

        MvcResult result = this.mockMvc.perform(get("/api/account"))
                .andExpect(status().isOk())
                .andReturn();

        List accounts = parseResponseList(result, Account.class);
        assertThat(accounts).hasSize(0);
    }

    @Test
    void findAccountById() throws Exception {
        String id = "thisistheid";
        given(accountRepository.findById(id)).willReturn(Optional.of(account("name", id)));

        MvcResult result = this.mockMvc.perform(get("/api/account/{id}", id))
                .andExpect(status().isOk())
                .andReturn();

        Account account = parseResponse(result, Account.class);
        assertThat(account.getId()).isEqualTo(id);
    }

    @Test
    void findAccountFails() throws Exception {
        String id = "thisistheid";
        given(accountRepository.findById(any())).willReturn(Optional.empty());

        this.mockMvc.perform(get("/api/account/{id}", id))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void insertsAccount() throws Exception {
        String name = "accountName";
        String id = "id123";
        AccountController.CreateAccountRequest request = new AccountController.CreateAccountRequest(name);
        given(accountRepository.insert((Account) any())).willReturn(account(name, id));

        MvcResult result = this.mockMvc.perform(post("/api/account")
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Account savedAccount = parseResponse(result, Account.class);
        assertThat(savedAccount.getName()).isEqualTo(name);
        assertThat(savedAccount.getId()).isEqualTo(id);
    }

    @Test
    void insertsAccountWithEmptyNameFails() throws Exception {
        String name = "";
        AccountController.CreateAccountRequest request = new AccountController.CreateAccountRequest(name);

        this.mockMvc.perform(post("/api/account")
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAccount() throws Exception {
        String name = "accountName";
        String id = "id123";
        String newName = "updated";
        AccountController.UpdateAccountRequest request = new AccountController.UpdateAccountRequest(newName);
        given(accountRepository.findById(id)).willReturn(Optional.of(account(name, id)));
        given(accountRepository.existsById(id)).willReturn(true);
        given(accountRepository.save(any())).willReturn(account(newName, id));

        MvcResult result = this.mockMvc.perform(put("/api/account/{id}", id)
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Account updatedAccount = parseResponse(result, Account.class);
        assertThat(updatedAccount.getName()).isEqualTo(newName);
        assertThat(updatedAccount.getId()).isEqualTo(id);
    }

    @Test
    void updateAccountFailsIfAccountNotExisting() throws Exception {
        String id = "id123";
        String newName = "updated";
        AccountController.UpdateAccountRequest request = new AccountController.UpdateAccountRequest(newName);
        given(accountRepository.findById(id)).willReturn(Optional.empty());

        this.mockMvc.perform(put("/api/account/{id}", id)
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void insertsEvent() throws Exception {
        String id = "id123";
        String type = "EVNT HPND";
        AccountController.CreateEventRequest request = new AccountController.CreateEventRequest(type);

        Account account = account("name", id);

        given(accountRepository.findById(id)).willReturn(Optional.of(account));
        given(eventRepository.insert((Event) any())).willAnswer(ans -> ans.getArgument(0));
        given(accountRepository.save(any())).willAnswer(ans -> ans.getArgument(0));

        MvcResult result = this.mockMvc.perform(post("/api/account/{id}/event", id)
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Event insertedEvent = parseResponse(result, Event.class);
        assertThat(insertedEvent.getType()).isEqualTo(type);
        assertThat(insertedEvent.getHappenedAt().getDayOfYear()).isEqualTo(LocalDate.now().getDayOfYear());

        assertThat(account.getStatistics()).hasSize(1);
        assertThat(account.getStatistics().get(0).getType()).isEqualTo(type);
    }

    @Test
    void insertsEventFailsIfAccountNotFound() throws Exception {
        String id = "id123";
        String type = "EVNT HPND";
        AccountController.CreateEventRequest request = new AccountController.CreateEventRequest(type);

        given(accountRepository.findById(id)).willReturn(Optional.empty());

        this.mockMvc.perform(post("/api/account/{id}/event", id)
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void insertEventAddsNewStatistic() throws Exception {
        String id = "id123";
        String type = "EVNT HPND";
        AccountController.CreateEventRequest request = new AccountController.CreateEventRequest(type);

        Account account = account("name", id);
        account.addStatistic(new Statistic("already exisitng", LocalDate.now(), 1L));

        given(accountRepository.findById(id)).willReturn(Optional.of(account));
        given(eventRepository.insert((Event) any())).willAnswer(ans -> ans.getArgument(0));
        given(accountRepository.save(any())).willAnswer(ans -> ans.getArgument(0));

        MvcResult result = this.mockMvc.perform(post("/api/account/{id}/event", id)
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Event insertedEvent = parseResponse(result, Event.class);
        assertThat(account.getStatistics()).hasSize(2);
        assertThat(account.getStatistics().get(0).getType()).isEqualTo(type);
    }

    @Test
    void insertEventCountsIncreasestatistic() throws Exception {
        String id = "id123";
        String type = "EVNT HPND";
        AccountController.CreateEventRequest request = new AccountController.CreateEventRequest(type);

        Account account = account("name", id);
        account.addStatistic(new Statistic(type, LocalDate.now(), 1L));

        given(accountRepository.findById(id)).willReturn(Optional.of(account));
        given(eventRepository.insert((Event) any())).willAnswer(ans -> ans.getArgument(0));
        given(accountRepository.save(any())).willAnswer(ans -> ans.getArgument(0));

        MvcResult result = this.mockMvc.perform(post("/api/account/{id}/event", id)
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Event insertedEvent = parseResponse(result, Event.class);
        assertThat(account.getStatistics()).hasSize(1);
        assertThat(account.getStatistics().get(0).getCount()).isEqualTo(2);
    }

    //get event for account
    //statistics
    Account account(String name, String id) {
        Account account = new Account(name);
        ReflectionTestUtils.setField(account, "id", id);
        return account;
    }
}
