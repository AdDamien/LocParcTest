package MNS.LocParc;

import MNS.LocParc.dao.UtilisateurDao;
import MNS.LocParc.models.Utilisateur;
import MNS.LocParc.securite.JwtUtils;
import MNS.LocParc.securite.MonUserDetails;
import MNS.LocParc.securite.MonUserDetailsService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.io.ByteArrayOutputStream;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class LocParcApplicationTests {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@MockBean
	private MonUserDetailsService userDetailsService;

	@MockBean
	private JwtUtils jwtUtils;

	@MockBean
	private AuthenticationManager authenticationManager;

	@MockBean
	UtilisateurDao utilisateurDao;

	@MockBean
	Utilisateur utilisateur;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
	}

	@Test
	void contextLoads() {
	}

	@Test
	public void testConnexionReussi() throws Exception {
		String mockAccessToken = "jwt123456789azerty"; // Supposons qu'une chaîne factice est renvoyée par jwtUtils.generateJwt()

		MonUserDetails userDetails = new MonUserDetails(utilisateur);
		when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
				.thenReturn(new UsernamePasswordAuthenticationToken(userDetails, "motdepasse"));

		// Définir le comportement attendu du mock userDetailsService pour charger les détails de l'utilisateur
		when(userDetailsService.loadUserByUsername("jacksparrow@example.com")).thenReturn(userDetails);

		// Définir le comportement attendu du mock jwtUtils pour générer le jeton d'accès
		when(jwtUtils.generateJwt(userDetails)).thenReturn(mockAccessToken);

		// Envoyer une requête POST pour la connexion avec les informations d'identification valides
		mvc.perform(MockMvcRequestBuilders.post("/connexion")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\": \"jacksparrow@example.com\", \"motDePasse\": \"motdepasse\"}"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(mockAccessToken));
	}

	@Test
	public void testConnexionEchouee_BadCredentials() throws Exception {
		// Définir le comportement attendu du mock authenticationManager pour un échec d'authentification (Bad Credentials)
		when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
				.thenThrow(new BadCredentialsException("Identifiants invalides"));

		// Envoyer une requête POST pour la connexion avec des informations d'identification incorrectes
		mvc.perform(MockMvcRequestBuilders.post("/connexion")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\": \"john@example.com\", \"motDePasse\": \"mauvaisMotDePasse\"}"))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}


	@Test
	@WithMockUser(roles = {"UTILISATEUR"})
	public void utilisateurAppelGetUtilisateurById_403Attendu() throws Exception {
		mvc.perform(get("/utilisateur/id")).andExpect(status().isForbidden());
	}
	@Test
	@WithMockUser(roles = {"ADMINISTRATEUR"})
	public void administrateurAppelGetUtilisateurByIdNonExistant_404Attendu() throws Exception {
		// Supposons que l'ID 1000 ne correspond à aucun utilisateur dans la base de données
		int userId = 1000;

		// Définir le comportement attendu du mock utilisateurDao

		when(utilisateurDao.findById(userId)).thenReturn(Optional.empty());

		// Effectuer la requête GET pour récupérer l'utilisateur par son ID
		mvc.perform(MockMvcRequestBuilders.get("/utilisateur/{id}", userId))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	@WithMockUser(roles = {"GESTIONNAIRE"})
	public void gestionnaireAppelGetUtilisateurByIdNonExistant_404Attendu() throws Exception {
		// Supposons que l'ID 1000 ne correspond à aucun utilisateur dans la base de données
		int userId = 1000;

		// Définir le comportement attendu du mock utilisateurDao

		when(utilisateurDao.findById(userId)).thenReturn(Optional.empty());

		// Effectuer la requête GET pour récupérer l'utilisateur par son ID
		mvc.perform(MockMvcRequestBuilders.get("/utilisateur/{id}", userId))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	@WithMockUser(roles = { "ADMINISTRATEUR" })
	public void administrateurAppelGetUtilisateurByIdExistant_200Attendu() throws Exception {
		// Supposons que l'ID 3 correspond à un utilisateur existant dans la base de données
		int userId = 3;

		// Créer un utilisateur factice pour simuler la réponse du mock utilisateurDao
		Utilisateur utilisateurExistant = new Utilisateur();
		utilisateurExistant.setId(userId);
		utilisateurExistant.setNom("ADAM");
		utilisateurExistant.setPrenom("Damien");


		// Définir le comportement attendu du mock utilisateurDao
		when(utilisateurDao.findById(userId)).thenReturn(Optional.of(utilisateurExistant));

		// Effectuer la requête GET pour récupérer l'utilisateur par son ID et comparer les information obtenues
		mvc.perform(MockMvcRequestBuilders.get("/utilisateur/{id}", userId))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userId))
				.andExpect(MockMvcResultMatchers.jsonPath("$.nom").value("SPARROW"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.prenom").value("Damien"));
	}

	@Test
	void utilisateurNonConnecteAppelUrlListeUtilisateur_403Attendu() throws Exception {
		mvc.perform(get("/utilisateur")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = {"UTILISATEUR"})
	void utilisateurConnecteAppelUrlListeUtilisateur_403Attendu() throws Exception {
		mvc.perform(get("/utilisateur")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = {"ADMINISTRATEUR"})
	void administrateurAppelUrlListeUtilisateur_OKAttendu() throws Exception {
		mvc.perform(get("/utilisateur")).andExpect(status().isOk());
	}
	@Test
	@WithMockUser(roles = {"GESTIONNAIRE"})
	void gestionnaireAppelUrlListeUtilisateur_OKAttendu() throws Exception {
		mvc.perform(get("/utilisateur")).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(roles = {"UTILISATEUR"})
	void utilisateurAppelUrlListeBonMateriel_OKAttendu() throws Exception {
		mvc.perform(get("/liste-bonmateriel")).andExpect(status().isOk());
	}
	@Test
	@WithMockUser(roles = {"GESTIONNAIRE"})
	void gestionnaireAppelUrlListeBonMateriel_OKAttendu() throws Exception {
		mvc.perform(get("/liste-bonmateriel")).andExpect(status().isOk());
	}
	@Test
	@WithMockUser(roles = {"ADMINISTRATEUR"})
	void administrateurAppelUrlListeBonMateriel_OKAttendu() throws Exception {
		mvc.perform(get("/liste-bonmateriel")).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(roles = {"ADMINISTRATEUR"})
	void administrateurAppelUrlBadMateriel_OKAttendu() throws Exception {
		mvc.perform(get("/badmateriel")).andExpect(status().isOk());
	}
	@Test
	@WithMockUser(roles = {"GESTIONNAIRE"})
	void gestionnaireAppelUrlBadMateriel_OKAttendu() throws Exception {
		mvc.perform(get("/badmateriel")).andExpect(status().isOk());
	}
	@Test
	@WithMockUser(roles = {"Utilisateur"})
	void utilisateurAppelUrlBadMateriel_ForbiddenAttendu() throws Exception {
		mvc.perform(get("/badmateriel")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = {"ADMINISTRATEUR"})
	void testImportUtilisateurs_ExtensionFichierInvalide() throws Exception {
		// Créer un fichier fictif avec une extension invalide (test.jpg)
		byte[] fichierInvalide = new byte[0];
		MockMultipartFile mockMultipartFile = new MockMultipartFile(
				"fichier",
				"test.jpg",
				"image/jpeg",
				fichierInvalide
		);

		// Exécuter la requête POST pour importer les utilisateurs avec le fichier de test
		MvcResult result = mvc.perform(MockMvcRequestBuilders.multipart("/import-utilisateurs")
						.file(mockMultipartFile))
				.andExpect(status().isBadRequest()) // Attend une erreur 400 Bad Request
				.andReturn();
	}

	@Test
	@WithMockUser(roles = {"ADMINISTRATEUR"})
	void testLectureImportUtilisateurs() throws Exception {
		// Créez un fichier de test en mémoire avec les données
		Workbook classeur = new XSSFWorkbook();
		Sheet feuille1 = classeur.createSheet("Feuille1");
		Row ligne1 = feuille1.createRow(0);
		Cell cellule2 = ligne1.createCell(0);
		cellule2.setCellValue("John");
		Cell cellule3 = ligne1.createCell(1);
		cellule3.setCellValue("Doe");
		Cell cellule4 = ligne1.createCell(2);
		cellule4.setCellValue("john@example.com");
		Cell cellule5 = ligne1.createCell(3);
		cellule5.setCellValue("987654321");

		Row ligne2 = feuille1.createRow(1);
		Cell cellule6 = ligne2.createCell(0);
		cellule6.setCellValue("Jane");
		Cell cellule7 = ligne2.createCell(1);
		cellule7.setCellValue("Doe");
		Cell cellule8 = ligne2.createCell(2);
		cellule8.setCellValue("jane@example.com");
		Cell cellule9 = ligne2.createCell(3);
		cellule9.setCellValue("123456789");

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		classeur.write(outputStream);

		MockMultipartFile mockMultipartFile = new MockMultipartFile(
				"fichier",
				"test.xlsx",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
				outputStream.toByteArray()
		);

		MvcResult result = mvc.perform(MockMvcRequestBuilders.multipart("/import-utilisateurs")
						.file(mockMultipartFile))
				.andExpect(status().isOk())
				.andReturn();


		String response = result.getResponse().getContentAsString();
		assertThat(response).contains("John");
		assertThat(response).contains("Doe");
		assertThat(response).contains("john@example.com");
		assertThat(response).contains("987654321");
		assertThat(response).contains("Jane");
		assertThat(response).contains("Doe");
		assertThat(response).contains("jane@example.com");
		assertThat(response).contains("123456789"); // Vérifiez si le résultat contient la valeur

		// Fermez le classeur et supprimez les ressources après le test
		classeur.close();
		outputStream.close();
	}

}


