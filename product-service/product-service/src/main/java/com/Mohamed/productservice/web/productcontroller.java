package com.Mohamed.productservice.web;

import java.util.List;

import com.Mohamed.productservice.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.ParameterizedTypeReference;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@EnableMethodSecurity
@RequiredArgsConstructor
@Controller
@Slf4j
public class productcontroller{


    private final OAuth2AuthorizedClientService oauth2ClientService;
    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;




    @GetMapping("/")
    //@PreAuthorize("hasRole('client-user')")
    public String index(){
        return "index";
    }
    @GetMapping("/products")
    //@PreAuthorize("hasRole('client-admin')")
    public String products(Model model,@AuthenticationPrincipal OidcUser principal
    ) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken)
                authentication;

        OAuth2AuthorizedClient oauth2Client =
                oauth2ClientService.loadAuthorizedClient(oauthToken.
                        getAuthorizedClientRegistrationId(), oauthToken.getName());

        String jwtAccessToken = oauth2Client.getAccessToken().getTokenValue();
        System.out.println("jwtAccessToken = " + jwtAccessToken);


        System.out.println("Principal = " + principal);

        OidcIdToken idToken = principal.getIdToken();
        String idTokenValue = idToken.getTokenValue();
        System.out.println("idTokenValue = " + idTokenValue);
        model.addAttribute("products",productRepository.findAll());
        return "products";
    }

    @GetMapping("/suppliers")
    public String suppliers(Model model,@AuthenticationPrincipal OidcUser principal
    ) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken)
                authentication;

        OAuth2AuthorizedClient oauth2Client =
                oauth2ClientService.loadAuthorizedClient(oauthToken.
                        getAuthorizedClientRegistrationId(), oauthToken.getName());

        String jwtAccessToken = oauth2Client.getAccessToken().getTokenValue();
        System.out.println("jwtAccessToken = " + jwtAccessToken);


        System.out.println("Principal = " + principal);

        OidcIdToken idToken = principal.getIdToken();
        String idTokenValue = idToken.getTokenValue();
        System.out.println("idTokenValue = " + idTokenValue);
        String url = "http://localhost:8082/all";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtAccessToken);

        HttpEntity<List<Supplier>> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Supplier>> responseEntity = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
                });

        List<Supplier> pageSuppliers = responseEntity.getBody();
        log.info(pageSuppliers.toString());

        model.addAttribute("suppliers",pageSuppliers);
        return "suppliers";
    }
   /* @GetMapping("/all")
    public String allsuppliers(Model model,@AuthenticationPrincipal OidcUser principal
    ) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken)
                authentication;

        OAuth2AuthorizedClient oauth2Client =
                oauth2ClientService.loadAuthorizedClient(oauthToken.
                        getAuthorizedClientRegistrationId(), oauthToken.getName());

        String jwtAccessToken = oauth2Client.getAccessToken().getTokenValue();
        System.out.println("jwtAccessToken = " + jwtAccessToken);


        System.out.println("Principal = " + principal);

        OidcIdToken idToken = principal.getIdToken();
        String idTokenValue = idToken.getTokenValue();
        System.out.println("idTokenValue = " + idTokenValue);



        String url = "http://localhost:8083/suppliers";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtAccessToken);
        HttpEntity<List<Supplier>> entity = new HttpEntity<>(headers);
        ResponseEntity<PagedModel<Supplier>>response = restTemplate.exchange(url,
                HttpMethod.GET, entity, new ParameterizedTypeReference<PagedModel<Supplier>>()
                {});

        model.addAttribute("supplier",response.getBody().toString());
        return "supphateoas";
    }
    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception e, Model model){
        model.addAttribute("errorMessage","problème d'autorisation");
        return "errors";
    }


    /*
     * @GetMapping("/jwt")
     *
     * @ResponseBody public Map<String,String> map(HttpServletRequest request){
     * KeycloakAuthenticationToken token =(KeycloakAuthenticationToken)
     * request.getUserPrincipal(); KeycloakPrincipal
     * principal=(KeycloakPrincipal)token.getPrincipal(); KeycloakSecurityContext
     * keycloakSecurityContext=principal.getKeycloakSecurityContext();
     * Map<String,String> map = new HashMap<>(); map.put("access_token",
     * keycloakSecurityContext.getTokenString());
     * return map;
     * }
     */
}
@Data
@AllArgsConstructor
@NoArgsConstructor
class Supplier{
    private Long id;
    private String nom;
    private String description;


}
