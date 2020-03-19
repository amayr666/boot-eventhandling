package com.amayr.demo.account.support;

import com.amayr.demo.account.Account;
import com.amayr.demo.account.Statistic;
import com.amayr.demo.event.Event;
import com.amayr.demo.event.support.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
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
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
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

    private static String accountId = "aa11bb22cc33";
    private static String accountName = "Example Account 123###";
    private static String eventType = "Data Imported";

    @Test
    void findAllAccounts() throws Exception {
        given(accountRepository.findAll()).willReturn(Arrays.asList(account("acc1", "aabbcc111"), account("acc2", "bbcc2233")));

        MvcResult result = this.mockMvc.perform(get("/api/account"))
                .andExpect(status().isOk())
                .andDo(document("findAllAccounts",
                        responseFields(
                                accountFields(true)
                        )))
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
        given(accountRepository.findById(accountId)).willReturn(Optional.of(account(accountName, accountId)));

        MvcResult result = this.mockMvc.perform(get("/api/account/{id}", accountId))
                .andExpect(status().isOk())
                .andDo(document("findAccountById",
                        pathParameters(
                                parameterWithName("id").description("Id of the account")
                        ),
                        responseFields(
                                accountFields(false)
                        )))
                .andReturn();

        Account account = parseResponse(result, Account.class);
        assertThat(account.getId()).isEqualTo(accountId);
    }

    @Test
    void findAccountFails() throws Exception {
        given(accountRepository.findById(any())).willReturn(Optional.empty());

        this.mockMvc.perform(get("/api/account/{id}", accountId))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void insertsAccount() throws Exception {
        AccountController.CreateAccountRequest request = new AccountController.CreateAccountRequest(accountName);
        given(accountRepository.insert((Account) any())).willReturn(account(accountName, accountId));

        MvcResult result = this.mockMvc.perform(post("/api/account")
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("insertAccount",
                        requestFields(
                                fieldWithPath("name").description("Name for the new account")
                        ),
                        responseFields(
                                accountFields(false)
                        )))
                .andReturn();

        Account savedAccount = parseResponse(result, Account.class);
        assertThat(savedAccount.getName()).isEqualTo(accountName);
        assertThat(savedAccount.getId()).isEqualTo(accountId);
    }

    @Test
    void insertsAccountWithEmptyNameFails() throws Exception {
        AccountController.CreateAccountRequest request = new AccountController.CreateAccountRequest("");

        this.mockMvc.perform(post("/api/account")
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAccount() throws Exception {
        String newName = "UPDATED account name";
        AccountController.UpdateAccountRequest request = new AccountController.UpdateAccountRequest(newName);
        given(accountRepository.findById(accountId)).willReturn(Optional.of(account(accountName, accountId)));
        given(accountRepository.existsById(accountId)).willReturn(true);
        given(accountRepository.save(any())).willReturn(account(newName, accountId));

        MvcResult result = this.mockMvc.perform(put("/api/account/{id}", accountId)
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("updateAccount",
                        pathParameters(
                                parameterWithName("id").description("Id of account")
                        ),
                        requestFields(
                                fieldWithPath("name").description("New name for the given account")
                        ),
                        responseFields(
                                accountFields(false)
                        )))
                .andReturn();

        Account updatedAccount = parseResponse(result, Account.class);
        assertThat(updatedAccount.getName()).isEqualTo(newName);
        assertThat(updatedAccount.getId()).isEqualTo(accountId);
    }

    @Test
    void updateAccountFailsIfAccountNotExisting() throws Exception {
        AccountController.UpdateAccountRequest request = new AccountController.UpdateAccountRequest(accountName);
        given(accountRepository.findById(accountId)).willReturn(Optional.empty());

        this.mockMvc.perform(put("/api/account/{id}", accountId)
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void insertsEvent() throws Exception {
        AccountController.CreateEventRequest request = new AccountController.CreateEventRequest(eventType);

        Account account = account("name", accountId);

        given(accountRepository.findById(accountId)).willReturn(Optional.of(account));
        given(eventRepository.insert((Event) any())).willAnswer(ans -> {
            Event event = ans.getArgument(0);
            ReflectionTestUtils.setField(event, "id", "eevveenntt12");
            return event;
        });
        given(accountRepository.save(any())).willAnswer(ans -> ans.getArgument(0));

        MvcResult result = this.mockMvc.perform(post("/api/account/{id}/event", accountId)
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("insertEvent",
                        pathParameters(
                                parameterWithName("id").description("Id of account")
                        ),
                        requestFields(
                                fieldWithPath("type").description("Type of the event")
                        ),
                        responseFields(
                                eventFields(false)
                        )))
                .andReturn();

        Event insertedEvent = parseResponse(result, Event.class);
        assertThat(insertedEvent.getType()).isEqualTo(eventType);
        assertThat(insertedEvent.getHappenedAt().getDayOfYear()).isEqualTo(LocalDate.now().getDayOfYear());

        assertThat(account.getStatistics()).hasSize(1);
        assertThat(account.getStatistics().get(0).getType()).isEqualTo(eventType);
    }

    @Test
    void insertsEventFailsIfAccountNotFound() throws Exception {
        AccountController.CreateEventRequest request = new AccountController.CreateEventRequest(eventType);

        given(accountRepository.findById(accountId)).willReturn(Optional.empty());

        this.mockMvc.perform(post("/api/account/{id}/event", accountId)
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void insertEventAddsNewStatistic() throws Exception {
        String newEventType = "Another event type";
        AccountController.CreateEventRequest request = new AccountController.CreateEventRequest(newEventType);

        Account account = account(accountName, accountId);
        account.addStatistic(new Statistic(eventType, LocalDate.now(), 1L));

        given(accountRepository.findById(accountId)).willReturn(Optional.of(account));
        given(eventRepository.insert((Event) any())).willAnswer(ans -> ans.getArgument(0));
        given(accountRepository.save(any())).willAnswer(ans -> ans.getArgument(0));

        MvcResult result = this.mockMvc.perform(post("/api/account/{id}/event", accountId)
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        parseResponse(result, Event.class);
        assertThat(account.getStatistics()).hasSize(2);
        assertThat(account.getStatistics().get(0).getType()).isEqualTo(newEventType);
    }

    @Test
    void insertEventCountsIncreasestatistic() throws Exception {
        AccountController.CreateEventRequest request = new AccountController.CreateEventRequest(eventType);

        Account account = account(accountName, accountId);
        account.addStatistic(new Statistic(eventType, LocalDate.now(), 1L));

        given(accountRepository.findById(accountId)).willReturn(Optional.of(account));
        given(eventRepository.insert((Event) any())).willAnswer(ans -> ans.getArgument(0));
        given(accountRepository.save(any())).willAnswer(ans -> ans.getArgument(0));

        this.mockMvc.perform(post("/api/account/{id}/event", accountId)
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(account.getStatistics()).hasSize(1);
        assertThat(account.getStatistics().get(0).getCount()).isEqualTo(2);
    }

    @Test
    void getEventForAccount() throws Exception {
        Account account = account(accountName, accountId);
        Event event = new Event(accountId, eventType);
        ReflectionTestUtils.setField(event, "id", "eevvnntt11dd");

        given(accountRepository.findById(accountId)).willReturn(Optional.of(account));
        given(eventRepository.findEventByAccountId(accountId)).willReturn(Arrays.asList(event));

        MvcResult result = this.mockMvc.perform(get("/api/account/{id}/event", accountId))
                .andExpect(status().isOk())
                .andDo(document("getEventForAccount",
                        pathParameters(
                                parameterWithName("id").description("Account id")
                        ),
                        responseFields(
                                eventFields(true)
                        )))
                .andReturn();

        List<Event> receivedEvent = parseResponseList(result, Event.class);
        assertThat(receivedEvent).hasSize(1);
    }

    @Test
    void getStatisticsForAccount() throws Exception {
        Account account = account(accountName, accountId);
        account.addStatistic(new Statistic(eventType, LocalDate.now(), 2L));

        given(accountRepository.findById(accountId)).willReturn(Optional.of(account));

        MvcResult result = this.mockMvc.perform(get("/api/account/{id}/statistic", accountId))
                .andExpect(status().isOk())
                .andDo(document("getStatisticsForAccount",
                        pathParameters(
                                parameterWithName("id").description("Account id")
                        ),
                        responseFields(
                                fieldWithPath("[].day").description("The day of the event"),
                                fieldWithPath("[].type").description("The type of the event"),
                                fieldWithPath("[].count").description("Amount event happened grouped by day and type")
                        )))
                .andReturn();
        List<Statistic> statistics = parseResponseList(result, Statistic.class);
        assertThat(statistics).hasSize(1);
    }

    Account account(String name, String id) {
        Account account = new Account(name);
        ReflectionTestUtils.setField(account, "id", id);
        return account;
    }

    private FieldDescriptor[] accountFields(boolean asColleciton) {
        if (asColleciton) {
            return new FieldDescriptor[]{
                    fieldWithPath("[].id").description("Unique identifier of an account").type(JsonFieldType.STRING),
                    fieldWithPath("[].name").description("Unique name of an account. Must not be empty")};
        } else {
            return new FieldDescriptor[]{
                    fieldWithPath("id").description("Unique identifier of an account").type(JsonFieldType.STRING),
                    fieldWithPath("name").description("Unique name of an account. Must not be empty")};
        }
    }

    private FieldDescriptor[] eventFields(boolean asCollection) {
        if (asCollection) {
            return new FieldDescriptor[]{
                    fieldWithPath("[].id").description("Id of the event"),
                    fieldWithPath("[].happenedAt").description("Date and time of the event occurance"),
                    fieldWithPath("[].type").description("Type of the event. Any string, must not be empty")
            };
        } else {
            return new FieldDescriptor[]{
                    fieldWithPath("id").description("Id of the event"),
                    fieldWithPath("happenedAt").description("Date and time of the event occurance"),
                    fieldWithPath("type").description("Type of the event. Any string, must not be empty")
            };
        }
    }
}
