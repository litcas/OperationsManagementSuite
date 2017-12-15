package com.rengu.operationsoanagementsuite.Controller;

import com.rengu.operationsoanagementsuite.Service.DeviceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(DeviceController.class)
@AutoConfigureRestDocs(outputDir = "target/snippets")
public class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DeviceService deviceService;

//    @Test
//    public void saveDevice() {
//    }
//
//    @Test
//    public void deleteDevice() {
//    }
//
//    @Test
//    public void updateDevice() {
//    }
//
//    @Test
//    public void getDevice() {
//    }

    @Test
    @WithMockUser(username = "amdin", password = "admin", roles = {"ADMIN", "USER"})
    public void getDevices() throws Exception {
        this.mockMvc
                .perform(get("/devices"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("devices"));
    }
}