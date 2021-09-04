package integration;

import com.chat.app.config.AppConfig;
import com.chat.app.config.SecurityConfig;
import com.chat.app.config.TestDataSourceConfig;
import com.chat.app.config.TestWebConfig;
import com.chat.app.controllers.UserController;
import com.chat.app.models.Dtos.UserDto;
import com.chat.app.models.UserDetails;
import com.chat.app.models.UserModel;
import com.chat.app.models.specs.UserSpec;
import com.chat.app.security.Jwt;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = { AppConfig.class, TestWebConfig.class, SecurityConfig.class, TestDataSourceConfig.class})
@WebAppConfiguration(value = "src/main/java/com/chat/app")
@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class Users {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Qualifier("test-datasource")
    @Autowired
    private DataSource dataSource;

    private MockMvc mockMvc;
    private static String adminToken, userToken;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setupData() {
        ResourceDatabasePopulator rdp = new ResourceDatabasePopulator();
        rdp.addScript(new ClassPathResource("integrationTestsSql/UsersData.sql"));
        rdp.execute(dataSource);
    }

    @BeforeAll
    public void setup() {
        UserModel admin = new UserModel("adminUser", "password", "ROLE_ADMIN");
        admin.setId(1);

        UserModel user = new UserModel("testUser", "password", "ROLE_USER");
        user.setId(2);

        adminToken = "Token " + Jwt.generate(new UserDetails(admin, Collections
                .singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        userToken = "Token " + Jwt.generate(new UserDetails(user, Collections
                .singletonList(new SimpleGrantedAuthority("ROLE_USER"))));

        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    public void assertConfig_assertUserController() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        assertNotNull(servletContext);
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(webApplicationContext.getBean("userController"));
    }

    private UserModel user = new UserModel("username", "password","ROLE_USER", "firstname",
            "lastname", 25, "Bulgaria");
    private UserDto userDto = new UserDto(user);

    private RequestBuilder createMediaRegisterRequest(String url, String role, String username, String token){
        MockHttpServletRequestBuilder request = post(url)
                .param("username", username)
                .param("password", user.getPassword())
                .param("repeatPassword", user.getPassword())
                .param("firstName", user.getFirstName())
                .param("lastName", user.getLastName())
                .param("age", String.valueOf(user.getAge()))
                .param("country", user.getCountry());

        if(token != null){
            request.header("Authorization", token);
        }

        userDto.setRole(role);
        userDto.setId(10);

        return  request;
    }

    @WithMockUser(value = "spring")
    @Test
    public void register() throws Exception {
        mockMvc.perform(createMediaRegisterRequest("/api/users/register", "ROLE_USER", "username", null))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(objectMapper.writeValueAsString(userDto))));

        checkDbForUser(userDto);
    }

    @WithMockUser(value = "spring")
    @Test
    public void registerAdmin() throws Exception {
        mockMvc.perform(createMediaRegisterRequest("/api/users/auth/registerAdmin", "ROLE_ADMIN", "username", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(objectMapper.writeValueAsString(userDto))));

        checkDbForUser(userDto);
    }

    @WithMockUser(value = "spring")
    @Test
    public void registerAdmin_WithUserThatIsNotAdmin_Unauthorized() throws Exception {
        mockMvc.perform(createMediaRegisterRequest("/api/users/auth/registerAdmin", "ROLE_ADMIN", "username", userToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Access is denied"));
    }

    @Test
    public void register_WhenUsernameIsTaken() throws Exception {
        RequestBuilder request = post("/api/users/register")
                .param("username", "testUser")
                .param("password", "password")
                .param("repeatPassword", "password");

        mockMvc.perform(createMediaRegisterRequest("/api/users/register", "ROLE_USER", "testUser", null))
                .andExpect(content().string(containsString("Username is already taken.")));
    }

    private void checkDbForUser(UserDto user) throws Exception{
        mockMvc.perform(get("/api/users/findById/" + user.getId()))
                .andExpect(content().string(objectMapper.writeValueAsString(user)));
    }

    @Test
    public void login() throws Exception {
        mockMvc.perform(post("/api/users/login")
            .contentType("Application/json")
            .content("{\"username\": \"adminUser\", \"password\": \"password\"}"))
            .andExpect(status().isOk());
    }

    @Test
    public void login_WithWrongPassword_ShouldThrow() throws Exception {
        mockMvc.perform(post("/api/users/login")
                .contentType("Application/json")
                .content("{\"username\": \"username\", \"password\": \"incorrect\"}"))
                .andExpect(status().is(401))
                .andExpect(content().string(containsString("Bad credentials")));
    }

    @Test
    public void login_WithWrongUsername_ShouldThrow() throws Exception {
        mockMvc.perform(post("/api/users/login")
                .contentType("Application/json")
                .content("{\"username\": \"incorrect\", \"password\": \"password\"}"))
                .andExpect(status().is(401))
                .andExpect(content().string(containsString("Bad credentials")));
    }

    @Test
    void findById() throws Exception {
        UserDto user = new UserDto(new UserModel("adminUser", "password", "ROLE_ADMIN",
                "firstName", "lastName", 25, "Bulgaria"));
        user.setId(1);

        checkDbForUser(user);
    }

    @Test
    void findById_WithNonExistentId() throws Exception {
        mockMvc.perform(get("/api/users/findById/222"))
                .andExpect(status().isNotFound());
    }

    @Test
    void changeUserInfo() throws Exception {
        UserSpec userSpec = new UserSpec(1, "newUsername", "newFirstName",
                "newLastName", 26, "newCountry");
        UserDto userDto = new UserDto(userSpec, "ROLE_ADMIN");

        mockMvc.perform(post("/api/users/auth/changeUserInfo")
                .header("Authorization", adminToken)
                .contentType("Application/json")
                .content(objectMapper.writeValueAsString(userSpec)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(userDto)));

        checkDbForUser(userDto);
    }
}
