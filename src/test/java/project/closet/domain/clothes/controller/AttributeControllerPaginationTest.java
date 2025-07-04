package project.closet.domain.clothes.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import project.closet.domain.clothes.entity.Attribute;
import project.closet.domain.clothes.repository.AttributeRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(roles = "USER")
class AttributeControllerPaginationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        attributeRepository.deleteAll();
        // Insert 5 sample attributes
        for (int i = 1; i <= 5; i++) {
            Attribute attr = new Attribute("name" + i, List.of("v" + i + "-a", "v" + i + "-b"));
            attributeRepository.save(attr);
        }
    }

    @Test
    void testFirstPage() throws Exception {
        mockMvc.perform(
                        get("/api/clothes/attribute-defs")
                                .param("limit", "2")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].name", is("name1")))
                .andExpect(jsonPath("$.data[1].name", is("name2")))
                .andExpect(jsonPath("$.hasNext", is(true)))
                .andExpect(jsonPath("$.nextCursor").isNotEmpty());
    }

    @Test
    void testSecondPageUsingCursor() throws Exception {
        // First request to get cursor
        String response = mockMvc.perform(
                        get("/api/clothes/attribute-defs")
                                .param("limit", "2")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String nextCursor = objectMapper.readTree(response).get("nextCursor").asText();

        // Second page
        mockMvc.perform(
                        get("/api/clothes/attribute-defs")
                                .param("limit", "2")
                                .param("cursor", nextCursor)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].name", is("name3")))
                .andExpect(jsonPath("$.data[1].name", is("name4")))
                .andExpect(jsonPath("$.hasNext", is(true)))
                .andExpect(jsonPath("$.nextCursor").isNotEmpty());
    }

    @Test
    void testLastPage() throws Exception {
        // Skip first 4 items
        String response1 = mockMvc.perform(
                        get("/api/clothes/attribute-defs")
                                .param("limit", "2")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String cursor1 = objectMapper.readTree(response1).get("nextCursor").asText();

        String response2 = mockMvc.perform(
                        get("/api/clothes/attribute-defs")
                                .param("limit", "2")
                                .param("cursor", cursor1)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String cursor2 = objectMapper.readTree(response2).get("nextCursor").asText();

        // Last page: only one item remains
        mockMvc.perform(
                        get("/api/clothes/attribute-defs")
                                .param("limit", "2")
                                .param("cursor", cursor2)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name", is("name5")))
                .andExpect(jsonPath("$.hasNext", is(false))); // no nextCursor check needed on last page
    }
}
