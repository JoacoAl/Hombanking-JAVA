package com.mindhub.homebanking.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@EnableWebSecurity
@Configuration
public class WebAuthorization implements WebMvcConfigurer {

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                /*all*/
                .antMatchers("/api/login").permitAll()
                .antMatchers("/web/index.html","/js/index.js","/style/index.css").permitAll()
                .antMatchers(HttpMethod.POST, "/api/clients").permitAll()
                .antMatchers(HttpMethod.POST, "/api/implementCardPayments").permitAll()

                /*admin*/
                .antMatchers("/web/manager.html","/js/manager.js", "/style/manager.css").hasAuthority("ADMIN")
                .antMatchers("/h2-console/**","/rest/**","/api/clients", "/api/accounts" ).hasAuthority("ADMIN")
                .antMatchers("/web/create-loan.html", "/js/create-loan.js","/style/create-loan.css").hasAuthority("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/createLoan").hasAuthority("ADMIN")



                /*client*/
                .antMatchers("/api/clients/current").hasAuthority("CLIENT")
                .antMatchers("/api/getCards").hasAuthority("CLIENT")
                .antMatchers("/api/getAccounts").hasAuthority("CLIENT")
                .antMatchers(HttpMethod.PUT, "/api/clients/current/cards").hasAuthority("CLIENT")
                .antMatchers(HttpMethod.PUT, "/api/clients/current/accounts").hasAuthority("CLIENT")
                .antMatchers(HttpMethod.POST, "/api/clients/current/accounts").hasAuthority("CLIENT")
                .antMatchers(HttpMethod.POST, "/api/clients/current/cards").hasAuthority("CLIENT")
                .antMatchers(HttpMethod.POST, "/api/transactions").hasAuthority("CLIENT")
                .antMatchers(HttpMethod.POST, "/api/loans").hasAuthority("CLIENT")
                .antMatchers(HttpMethod.POST, "/api/loan/payments").hasAuthority("CLIENT")
                .antMatchers(HttpMethod.GET, "/api/downloadPDF").hasAuthority("CLIENT")
                .antMatchers("/web/accounts.html", "/js/accounts.js","/style/accounts.css").hasAuthority("CLIENT")
                .antMatchers("/web/account.html", "/js/account.js", "/style/account.css").hasAuthority("CLIENT")
                .antMatchers("/web/cards.html", "/style/cards.css", "/js/cards.js").hasAuthority("CLIENT")
                .antMatchers("/web/create-card.html", "/js/create-card.js").hasAuthority("CLIENT")
                .antMatchers("/web/transfers.html", "/js/transfers.js","/style/transfers.css").hasAuthority("CLIENT")
                .antMatchers("/web/loan-application.html", "/js/loan-application.js","/style/loan-application.css").hasAuthority("CLIENT")
                .antMatchers("/web/pay-loan.html", "/js/pay-loan.js").hasAuthority("CLIENT");



        http.formLogin()
                .usernameParameter("email")
                .passwordParameter("password")
                .loginPage("/api/login");
        http.logout().logoutUrl("/api/logout").deleteCookies("JSSESIONID");

        //un tipo de seguridad que desabhilitamos porque vamos a utilizar tokens
        http.csrf().disable();

        http.cors(); //habilitar el cors en spring security

        //deshabilitar frameOptions para que se pueda acceder a h2-console
        http.headers().frameOptions().disable();

        // la autorizacion que tiene no es suficiente para, acceder a la ruta que esta tratando de ingresar
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // inicio de session exitoso, borramos las flags de la autenticacion
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // respuesta de error al fallo de inicio, 401
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // logout exitoso, 200
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());

        return http.build();
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session != null) {

            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

        }

    }

    //CORS= (Cross-Origin Resource Sharing) para permitir solicitudes desde orígenes diferentes al de tu servidor.
    @Override
    public void addCorsMappings(CorsRegistry registry) { //CorsRegistry para definir las reglas del Cors
        registry.addMapping("/**") //todas las rutas de la app
                .allowedOrigins("http://127.0.0.1:5500") //permitimos desde ese server
                .allowedMethods("GET", "POST", "PUT", "DELETE") //los metodos que permitimos
                .allowCredentials(true) //se permiten solicitudes con credenciales, como cookies o encabezados de autenticación
                .maxAge(3600); //tiempo maximo que el cache tiene que almacenar la cfg del cors
    }
}