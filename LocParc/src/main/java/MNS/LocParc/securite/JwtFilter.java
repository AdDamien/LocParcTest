package MNS.LocParc.securite;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

    @Component
    public class JwtFilter extends OncePerRequestFilter {

        @Autowired
        private MonUserDetailsService userDetailsService;
        @Autowired
        private JwtUtils jwtUtil;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String jwt = request.getHeader("Authorization");

            if (jwt != null && jwt.startsWith("Bearer ")){

                String token = jwt.substring(7);

                if (jwtUtil.isTokenValide(token)) {

                    Claims donnes = getData(token);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(donnes.getSubject());

                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            filterChain.doFilter(request,response);
        }
        public Claims getData(String jwt){
            return Jwts.parser()
                    .setSigningKey("azerty")
                    .parseClaimsJws(jwt)
                    .getBody();
        }
    }

