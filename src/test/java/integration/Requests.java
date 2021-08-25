package integration;

import com.chat.app.config.AppConfig;
import com.chat.app.config.SecurityConfig;
import com.chat.app.config.TestDataSourceConfig;
import com.chat.app.config.TestWebConfig;
import com.chat.app.controllers.RequestController;
import com.chat.app.models.Dtos.PageDto;
import com.chat.app.models.Dtos.RequestDto;
import com.chat.app.models.Dtos.UserDto;
import com.chat.app.models.UserDetails;
import com.chat.app.models.UserModel;
import com.chat.app.security.Jwt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
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
public class Requests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DataSource dataSource;

    private MockMvc mockMvc;
    private static String adminToken;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setupData() {
        ResourceDatabasePopulator rdp = new ResourceDatabasePopulator();
        rdp.addScript(new ClassPathResource("integrationTestsSql/ChatsData.sql"));
        rdp.addScript(new ClassPathResource("integrationTestsSql/RequestsData.sql"));
        rdp.execute(dataSource);
    }

    @BeforeAll
    public void setup() {
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
    public void assertConfig_assertUserController(){
        ServletContext servletContext = webApplicationContext.getServletContext();

        Assert.assertNotNull(servletContext);
        Assert.assertTrue(servletContext instanceof MockServletContext);
        Assert.assertNotNull(webApplicationContext.getBean("requestController"));
    }

    @Test
    public void addRequest_ShouldReturnChat_WhenChatAlreadyExists() throws Exception{
        MvcResult result = mockMvc.perform(post("/api/requests/auth/addRequest/2")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn();
        UserDto userDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        Assert.assertEquals(userDto.getChatWithUser().getId(), 1);

        mockMvc.perform(get("/api/requests/auth/findRequest/2")
                .header("Authorization", adminToken))
                .andExpect(content().string(containsString("")));
    }

    @Test
    public void findAll() throws Exception{
        MvcResult result = mockMvc.perform(get("/api/requests/auth/findAll/2")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn();

        PageDto<RequestDto> page = objectMapper.readValue(result.getResponse()
                .getContentAsString(), new TypeReference<PageDto<RequestDto>>(){});
        List<RequestDto> requests = page.getData();

        Assert.assertEquals(page.getCount(), 5);
        Assert.assertEquals(requests.size(), 2);
        Assert.assertEquals(requests.get(0).getId(), 2);
        Assert.assertEquals(requests.get(1).getId(), 1);
    }

    @Test
    public void findNextAll() throws Exception{
        MvcResult result = mockMvc.perform(get("/api/requests/auth/findAll/4/2021-08-26T18:17:44/1")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andReturn();

        PageDto<RequestDto> page = objectMapper.readValue(result.getResponse()
                .getContentAsString(), new TypeReference<PageDto<RequestDto>>(){});
        List<RequestDto> requests = page.getData();

        Assert.assertEquals(page.getCount(), 3);
        Assert.assertEquals(requests.size(), 3);
        Assert.assertEquals(requests.get(0).getId(), 7);
        Assert.assertEquals(requests.get(2).getId(), 3);
    }
}
