package com.jayantirai.tasktracker;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TaskTrackerApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    private String register(String user) throws Exception {
        String body = "{\"username\":\"" + user + "\",\"email\":\"" + user + "@x.com\",\"password\":\"password123\"}";
        String res = mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        JsonNode n = mapper.readTree(res);
        return "Bearer " + n.get("token").asText();
    }

    @Test
    void registerLoginAndOwnership() throws Exception {
        String alice = register("alice");
        String bob = register("bob");

        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"alice\",\"password\":\"password123\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.token").exists());
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"alice\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized());

        String created = mvc.perform(post("/api/tasks").header("Authorization", alice)
                .contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Write tests\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("TODO"))
                .andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(created).get("id").asLong();

        mvc.perform(get("/api/tasks/" + id).header("Authorization", alice)).andExpect(status().isOk());
        mvc.perform(get("/api/tasks/" + id).header("Authorization", bob)).andExpect(status().isNotFound());
        mvc.perform(put("/api/tasks/" + id).header("Authorization", alice)
                .contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Done\",\"status\":\"DONE\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("DONE"));
        mvc.perform(delete("/api/tasks/" + id).header("Authorization", bob)).andExpect(status().isNotFound());
        mvc.perform(delete("/api/tasks/" + id).header("Authorization", alice)).andExpect(status().isNoContent());
    }

    @Test
    void validationUnauthenticatedAndRoles() throws Exception {
        mvc.perform(get("/api/tasks")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"a\",\"email\":\"bad\",\"password\":\"x\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors.email").exists());
        String carol = register("carol");
        mvc.perform(post("/api/tasks").header("Authorization", carol)
                .contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/tasks/admin/all").header("Authorization", carol)).andExpect(status().isForbidden());
    }
}
