package com.example.taskone;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@WebServlet("/products")
public class ProductServlet extends HttpServlet {

    private static final String API_URL = "https://api.predic8.de/shop/v2/products/";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(API_URL);
        Response apiResponse = target.request(MediaType.APPLICATION_JSON).get();

        if (apiResponse.getStatus() == 200) {
            String json = apiResponse.readEntity(String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode productsNode = rootNode.path("products");

            List<Product> products = new ArrayList<>();
            if (productsNode.isArray()) {
                for (JsonNode productNode : productsNode) {
                    Product product = new Product();
                    product.setId(productNode.path("id").asText());
                    product.setName(productNode.path("name").asText());
                    products.add(product);
                }
            }

            request.setAttribute("products", products);
            request.getRequestDispatcher("/products.jsp").forward(request, response);
        } else {
            response.sendError(apiResponse.getStatus(), "Failed to fetch product data");
        }
    }

    public static class Product {
        private String id;
        private String name;
        private String category;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
