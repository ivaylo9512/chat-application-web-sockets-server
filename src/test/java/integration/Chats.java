package integration;

import com.chat.app.config.AppConfig;
import com.chat.app.config.SecurityConfig;
import com.chat.app.config.TestWebConfig;
import com.chat.app.controllers.ChatController;
import com.chat.app.models.Dtos.ChatDto;
import com.chat.app.models.Dtos.MessageDto;
import com.chat.app.models.Dtos.PageDto;
import com.chat.app.models.Dtos.SessionDto;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = { AppConfig.class, TestWebConfig.class, SecurityConfig.class })
@WebAppConfiguration(value = "src/main/java/com/chat/app")
@WebMvcTest(ChatController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
@Transactional
public class Chats {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private static String adminToken, userToken;

    @BeforeEach
    public void setupData() {
        ResourceDatabasePopulator rdp = new ResourceDatabasePopulator();
        rdp.addScript(new ClassPathResource("integrationTestsSql/UsersData.sql"));
        rdp.addScript(new ClassPathResource("integrationTestsSql/ChatsData.sql"));
        rdp.addScript(new ClassPathResource("integrationTestsSql/SessionsData.sql"));
        rdp.addScript(new ClassPathResource("integrationTestsSql/MessagesData.sql"));
        rdp.execute(dataSource);
    }

    @BeforeAll
    public void setup() {
        ResourceDatabasePopulator rdp = new ResourceDatabasePopulator();
        rdp.addScript(new ClassPathResource("integrationTestsSql/FilesData.sql"));
        rdp.execute(dataSource);

        UserModel admin = new UserModel("adminUser", "password", "ROLE_ADMIN");
        admin.setId(1);

        UserModel user = new UserModel("testUser", "password", "ROLE_USER");
        user.setId(3);

        adminToken = "Token " + Jwt.generate(new UserDetails(admin, Collections
                .singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        userToken = "Token " + Jwt.generate(new UserDetails(user, Collections
                .singletonList(new SimpleGrantedAuthority("ROLE_USER"))));

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    public void assertConfig_assertChatController() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        assertNotNull(servletContext);
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(webApplicationContext.getBean("chatController"));
    }

    @Test
    public void findUsersChat() throws Exception {
        String result = mockMvc.perform(get("/api/chats/auth/findByUser/2")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ChatDto chat = objectMapper.readValue(result, ChatDto.class);

        assertEquals(chat.getId(), 1);
        assertEquals(chat.getFirstUser().getId(), 1);
        assertEquals(chat.getSecondUser().getId(), 2);
    }

    @Test
    public void findChats() throws Exception {
        String response = mockMvc.perform(get("/api/chats/auth/findChats/3")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PageDto<ChatDto> page = objectMapper.readValue(response, new TypeReference<>(){});
        List<ChatDto> chats = page.getData();
        List<SessionDto> sessions = chats.get(1).getSessions();

        assertEquals(page.getCount(), 2);
        assertEquals(chats.size(), 3);

        assertEquals(chats.get(0).getId(), 2);
        assertEquals(chats.get(1).getId(), 1);
        assertEquals(chats.get(2).getId(), 11);

        assertEquals(sessions.get(0).getDate().toString(), "2021-09-18");
        assertEquals(sessions.get(1).getDate().toString(), "2021-09-17");
        assertEquals(sessions.get(2).getDate().toString(), "2021-09-16");

        MessageDto message = sessions.get(0).getMessages().get(0);
        assertEquals(message.getMessage(), "testMessage");
        assertEquals(message.getReceiverId(), 1);
        assertEquals(message.getTime().toString(), "23:57:16");
        assertEquals(message.getChatId(), 1);
        assertEquals(message.getSession().toString(), "2021-09-18");
    }

    @Test
    public void findNextChats() throws Exception {
        String response = mockMvc.perform(get("/api/chats/auth/findChats/3/2021-09-17 23:15:42/11")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PageDto<ChatDto> page = objectMapper.readValue(response, new TypeReference<>(){});
        List<ChatDto> chats = page.getData();

        assertEquals(page.getCount(), 1);
        assertEquals(chats.size(), 2);

        assertEquals(chats.get(0).getId(), 5);
        assertEquals(chats.get(1).getId(), 6);
    }

    @Test
    public void findChatsByName() throws Exception {
        String response = mockMvc.perform(get("/api/chats/auth/findChatsByName/2/first")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PageDto<ChatDto> page = objectMapper.readValue(response, new TypeReference<>(){});
        List<ChatDto> chats = page.getData();
        List<SessionDto> sessions = chats.get(0).getSessions();

        assertEquals(page.getCount(), 3);
        assertEquals(chats.size(), 2);
        assertEquals(chats.get(0).getId(), 1);
        assertEquals(chats.get(1).getId(), 6);

        assertEquals(sessions.get(0).getDate().toString(), "2021-09-18");
        assertEquals(sessions.get(1).getDate().toString(), "2021-09-17");
        assertEquals(sessions.get(2).getDate().toString(), "2021-09-16");

        MessageDto message = sessions.get(0).getMessages().get(0);
        assertEquals(message.getMessage(), "testMessage");
        assertEquals(message.getReceiverId(), 1);
        assertEquals(message.getTime().toString(), "23:57:16");
        assertEquals(message.getChatId(), 1);
        assertEquals(message.getSession().toString(), "2021-09-18");
    }

    @Test
    public void findNextChatsByName() throws Exception {
        String response = mockMvc.perform(get("/api/chats/auth/findChatsByName/2/first/First testB/1")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PageDto<ChatDto> page = objectMapper.readValue(response, new TypeReference<>(){});
        List<ChatDto> chats = page.getData();

        assertEquals(page.getCount(), 2);
        assertEquals(chats.size(), 2);
        assertEquals(chats.get(0).getId(), 6);
        assertEquals(chats.get(1).getId(), 2);
    }

    @Test
    public void findChatsByDefaultName() throws Exception {
        String response = mockMvc.perform(get("/api/chats/auth/findChatsByName/3")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PageDto<ChatDto> page = objectMapper.readValue(response, new TypeReference<>() {});
        List<ChatDto> chats = page.getData();

        assertEquals(page.getCount(), 5);
        assertEquals(chats.size(), 3);
        assertEquals(chats.get(0).getId(), 1);
        assertEquals(chats.get(1).getId(), 6);
        assertEquals(chats.get(2).getId(), 2);
    }

    @Test
    public void findNextChatsByDefaultName() throws Exception {
        String response = mockMvc.perform(get("/api/chats/auth/findChatsByName/3/FirstC Last/2")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PageDto<ChatDto> page = objectMapper.readValue(response, new TypeReference<>() {});
        List<ChatDto> chats = page.getData();

        assertEquals(page.getCount(), 2);
        assertEquals(chats.size(), 2);
        assertEquals(chats.get(0).getId(), 11);
        assertEquals(chats.get(1).getId(), 5);
    }

    @Test
    public void findUsersChat_WithIncorrectToken() throws Exception {
        mockMvc.perform(get("/api/chats/auth/findByUser/2")
                .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    public void findUsersChat_WithoutToken() throws Exception {
        mockMvc.perform(get("/api/chats/auth/findByUser/2"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    public void findNextChats_WithIncorrectToken() throws Exception {
        mockMvc.perform(get("/api/chats/auth/findChats/3/2021-09-17 23:15:42/11")
                .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    public void findNextChats_WithoutToken() throws Exception {
        mockMvc.perform(get("/api/chats/auth/findChats/3/2021-09-17 23:15:42/11"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }

    @Test
    public void findChats_WithIncorrectToken() throws Exception {
        mockMvc.perform(get("/api/chats/auth/findChats/3")
                .header("Authorization", "Token incorrect"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is incorrect"));
    }

    @Test
    public void findChats_WithoutToken() throws Exception {
        mockMvc.perform(get("/api/chats/auth/findChats/3"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Jwt token is missing"));
    }
}