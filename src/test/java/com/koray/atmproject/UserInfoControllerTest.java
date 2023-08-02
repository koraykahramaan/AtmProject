package com.koray.atmproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koray.atmproject.dto.UserInfoResponse;
import com.koray.atmproject.model.UserInfo;
import com.koray.atmproject.service.UserInfoService;
import com.koray.atmproject.util.UserInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class UserInfoControllerTest {

    private static final String END_POINT_PATH = "/api/v1/auth";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserInfoService userInfoService;
    @Test
    public void test_Register_Should_Return_400_Response() throws Exception {
        UserInfo newUser = UserInfo.builder().name("koray").email("ko").password("koray").build();

        String requestBody = objectMapper.writeValueAsString(newUser);

        mockMvc.perform(post(END_POINT_PATH + "/register")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().is4xxClientError())
                .andDo(print());

    }

    @Test
    public void test_Register_Should_Return_200_Response() throws Exception {
        UserInfo newUser = UserInfo.builder().name("koray").email("koray@gmail.com").password("koray").build();
        UserInfoMapper userInfoMapper = new UserInfoMapper();
        UserInfoResponse userInfoResponse = userInfoMapper.userInfoResponseToUserInfo(newUser);
        when(userInfoService.register(newUser)).thenReturn(userInfoResponse);
        String requestBody = objectMapper.writeValueAsString(newUser);

        mockMvc.perform(post(END_POINT_PATH + "/register")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.email").value("koray@gmail.com"))
                .andDo(print());
    }
}
