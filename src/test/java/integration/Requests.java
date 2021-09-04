package integration;

import com.chat.app.config.AppConfig;
import com.chat.app.config.SecurityConfig;
import com.chat.app.config.TestDataSourceConfig;
import com.chat.app.config.TestWebConfig;
import com.chat.app.controllers.RequestController;
import com.chat.app.models.Dtos.ChatDto;
import com.chat.app.models.Dtos.PageDto;
import com.chat.app.models.Dtos.RequestDto;
import com.chat.app.models.Dtos.UserDto;
import com.chat.app.models.UserDetails;
import com.chat.app.models.UserModel;
import com.chat.app.security.Jwt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = { AppConfig.class, TestWebConfig.class, SecurityConfig.class, TestDataSourceConfig.class})
@WebAppConfiguration(value = "src/main/java/com/chat/app")
@WebMvcTest(controllers = RequestController.class)
@Import(SecurityConfig.class)
@Transactional
@ActiveProfiles("test")
class Requests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DataSource dataSource;

    private MockMvc mockMvc;
    private static String adminToken;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setupData() {
        ResourceDatabasePopulator rdp = new ResourceDatabasePopulator();
        rdp.addScript(new ClassPathResource("integrationTestsSql/ChatsData.sql"));
        rdp.addScript(new ClassPathResource("integrationTestsSql/RequestsData.sql"));
        rdp.execute(dataSource);
    }

    @BeforeAll
    void setup() {
        UserModel admin = new UserModel("adminUser", "password", "ROLE_ADMIN");
        admin.setId(1);

        adminToken = "Token " + Jwt.generate(new UserDetails(admin, Collections
                .singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        ResourceDatabasePopulator rdp = new ResourceDatabasePopulator();
        rdp.addScript(new ClassPathResource("integrationTestsSql/UsersData.sql"));
        rdp.execute(dataSource);

        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void assertConfig_assertUserController(){
        ServletContext servletContext = webApplicationContext.getServletContext();

        assertNotNull(servletContext);
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(webApplicationContext.getBean("requestController"));
    }

    @Test
    void findAll() throws Exception{
        MvcResult result = mockMvc.perform(get("/api/requests/auth/findAll/2")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn();

        PageDto<RequestDto> page = objectMapper.readValue(result.getResponse()
                .getContentAsString(), new TypeReference<PageDto<RequestDto>>(){});
        List<RequestDto> requests = page.getData();

        assertEquals(page.getCount(), 5);
        assertEquals(requests.size(), 2);
        assertEquals(requests.get(0).getId(), 2);
        assertEquals(requests.get(1).getId(), 1);
    }

    @Test
    void findNextAll() throws Exception{
        MvcResult result = mockMvc.perform(get("/api/requests/auth/findAll/4/2021-08-26T18:17:44/1")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn();

        PageDto<RequestDto> page = objectMapper.readValue(result.getResponse()
                .getContentAsString(), new TypeReference<PageDto<RequestDto>>(){});
        List<RequestDto> requests = page.getData();

        assertEquals(page.getCount(), 3);
        assertEquals(requests.size(), 3);
        assertEquals(requests.get(0).getId(), 7);
        assertEquals(requests.get(2).getId(), 3);
    }

    @Test
    void findById() throws Exception{
        MvcResult result = mockMvc.perform(get("/api/requests/auth/findById/2")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn();

        RequestDto request = objectMapper.readValue(result.getResponse().getContentAsString(), RequestDto.class);
        assertEquals(request.getSender().getId(), 4);
        assertEquals(request.getReceiver().getId(), 1);
    }

    @Test
    void findById_WithNonExistentId_NotFound() throws Exception{
        mockMvc.perform(get("/api/requests/auth/findById/222")
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Request not found.")));
    }

    @Test
    void findById_WithRequestThatDoesNotBelongToUser_Unauthorized() throws Exception{
        mockMvc.perform(get("/api/requests/auth/findById/4")
                .header("Authorization", adminToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized."));
    }

    @Test
    void findRequest() throws Exception{
        MvcResult result = mockMvc.perform(get("/api/requests/auth/findByUser/4")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn();

        RequestDto request = objectMapper.readValue(result.getResponse().getContentAsString(), RequestDto.class);
        assertEquals(request.getId(), 2);
        assertEquals(request.getSender().getId(), 4);
        assertEquals(request.getReceiver().getId(), 1);
    }

    @Test
    void findRequest_WithNonExistentRequest_NotFound() throws Exception{
        mockMvc.perform(get("/api/requests/auth/findByUser/222")
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Request not found."));
    }

    @Test
    void addRequest_ShouldReturnChat_WhenChatAlreadyExists() throws Exception{
        MvcResult result = mockMvc.perform(post("/api/requests/auth/add/2")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn();

        UserDto userDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        assertEquals(userDto.getChatWithUser().getId(), 1);

        mockMvc.perform(get("/api/requests/auth/findByUser/2")
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Request not found."));
    }

    @Test
    void addRequest_WhenRequestIsNotAlreadyPresent() throws Exception{
        MvcResult result = mockMvc.perform(post("/api/requests/auth/add/8")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn();

        UserDto userDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        assertEquals(userDto.getRequestState(), "pending");

        MvcResult requestResult = mockMvc.perform(get("/api/requests/auth/findByUser/8")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn();

        RequestDto request = objectMapper.readValue(requestResult.getResponse().getContentAsString(), RequestDto.class);
        assertEquals(request.getSender().getId(), 1);
        assertEquals(request.getReceiver().getId(), 8);
    }

    @Test
    void addRequest_WhenRequestIsPresent_AndLoggedUserIsSender() throws Exception{
        MvcResult result = mockMvc.perform(post("/api/requests/auth/add/9")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn();

        UserDto user = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        assertEquals(user.getRequestState(), "pending");
        assertEquals(user.getRequestId(), 8);
        assertNull(user.getChatWithUser());
    }

    @Test
    void addRequest_WhenRequestIsPresent_AndLoggedUserIsReceiver_ShouldCreateChat_ShouldDeleteRequest() throws Exception{
        MvcResult result = mockMvc.perform(post("/api/requests/auth/add/3")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn();

        UserDto user = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        ChatDto chat = user.getChatWithUser();
        assertEquals(user.getRequestState(), "completed");
        assertEquals(chat.getFirstUser().getId(), 1);
        assertEquals(chat.getSecondUser().getId(), 3);

        mockMvc.perform(get("/api/requests/auth/findByUser/3")
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Request not found.")));

        MvcResult chatResult = mockMvc.perform(get("/api/chats/auth/findByUser/3")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn();

        ChatDto chatDto = objectMapper.readValue(chatResult.getResponse().getContentAsString(), ChatDto.class);

        assertEquals(chatDto.getFirstUser().getId(), 1);
        assertEquals(chatDto.getSecondUser().getId(), 3);
    }

    @Test
    void denyRequest_AndDelete() throws Exception{
        mockMvc.perform(post("/api/requests/auth/deny/2")
                .header("Authorization", adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/requests/auth/findById/2")
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Request not found."));
    }

    @Test
    void denyRequest_WithRequestThatDoesNotBelongToLoggedUser_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/requests/auth/deny/4")
                .header("Authorization", adminToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized."));
    }

    @Test
    void denyRequest_WithNonExistentRequest_NotFound() throws Exception{
        mockMvc.perform(post("/api/requests/auth/deny/222")
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Request not found."));
    }

    @Test
    void acceptRequest_ShouldCreateChat_ShouldDeleteRequest() throws Exception{
        MvcResult result = mockMvc.perform(post("/api/requests/auth/accept/3")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn();

        ChatDto chat = objectMapper.readValue(result.getResponse().getContentAsString(), ChatDto.class);

        assertEquals(chat.getFirstUser().getId(), 1);
        assertEquals(chat.getSecondUser().getId(), 5);

        mockMvc.perform(get("/api/requests/auth/findById/3")
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Request not found."));
    }

    @Test
    void acceptRequest_WithRequestThatDoesNotBelongToLoggedUser_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/requests/auth/accept/4")
                .header("Authorization", adminToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized."));
    }

    @Test
    void acceptRequest_WhenUserIsSender_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/requests/auth/accept/8")
                .header("Authorization", adminToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized."));
    }

    @Test
    void acceptRequest_WithNonExistentRequest_NotFound() throws Exception{
        mockMvc.perform(post("/api/requests/auth/accept/222")
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Request not found."));
    }

    @Test
    void acceptRequest_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/requests/auth/accept/2"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void acceptRequest_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/requests/auth/accept/2")
                .header("Authorization", "Token incorrect"))
                 .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    void denyRequest_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/requests/auth/deny/2"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void denyRequest_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/requests/auth/deny/2")
                        .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    void findById_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/requests/auth/findById/2"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void findById_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/requests/auth/findById/2")
                        .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    void findByUser_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/requests/auth/findByUser/2"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void findByUser_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/requests/auth/findByUser/2")
                        .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    void findAll_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/requests/auth/findAll/2"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void findAll_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/requests/auth/findAll/2")
                        .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    void addRequest_WithoutToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/requests/auth/add/2"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    void addRequest_WithIncorrectToken_Unauthorized() throws Exception{
        mockMvc.perform(post("/api/requests/auth/add/2")
                        .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }
}
