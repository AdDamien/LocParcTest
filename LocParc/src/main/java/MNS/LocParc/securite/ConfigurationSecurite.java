package MNS.LocParc.securite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import javax.sql.DataSource;
import java.util.Arrays;


@EnableWebSecurity
public class ConfigurationSecurite extends WebSecurityConfigurerAdapter {

   @Autowired
    private DataSource dataSource;

    @Autowired
    JwtFilter filtre;

    @Autowired
    private MonUserDetailsService monUserDetailsService;



    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(monUserDetailsService);
    }

    protected void configure(HttpSecurity http) throws Exception {
        http.cors().configurationSource(httpServletRequest -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.applyPermitDefaultValues();
                    corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT"));
                    corsConfiguration.setAllowedHeaders(
                            Arrays.asList("X-Requested-With", "Origin", "Content-Type",
                                    "Accept", "Authorization","Access-Control-Allow-Origin"));
                    return corsConfiguration;
                }).and()

                .csrf().disable()
                .authorizeRequests()

                .antMatchers("/connexion","/inscription","/utilisateur","/materiel","/login","/materiel-par-type/{nomType}","/pret-par-utilisateur","/liste-materiel-usertype","/materiel-par-etat/{nomEtat}","/materiel-par-modele/{nomModele}","/materiel-par-reference/{nomReference}","/image-profil/{idUtilisateur}","/liste-pret","/liste-etat","/materiel-count/{type}","/materiel-disponible","/materiel-count-disponible/{type}","/badmateriel","/pret-par-utilisateur/{id}","/liste-materiel","/liste-materiel-typer","/liste-bonmateriel","/image-materiel/","/utilisateur/{id}","/import-utilisateurs")
                .permitAll()
                .antMatchers("/admin/modifmateriel","/admin/**")
                .hasAnyRole("ADMINISTRATEUR","GESTIONNAIRE")
                .antMatchers("/profil/**")
                .hasAnyRole("ADMINISTRATEUR","UTILISATEUR","GESTIONNAIRE")
                .antMatchers("/**")
                .hasAnyRole("ADMINISTRATEUR","UTILISATEUR","GESTIONNAIRE")
                .anyRequest().authenticated()
                .and().exceptionHandling()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(filtre, UsernamePasswordAuthenticationFilter.class);


    }
    @Bean
    public PasswordEncoder creerMdpEncoder () {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws  Exception{
        return super.authenticationManagerBean();
    }
}






