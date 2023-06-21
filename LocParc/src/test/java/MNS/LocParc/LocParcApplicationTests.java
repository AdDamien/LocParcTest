package MNS.LocParc;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class LocParcApplicationTests {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

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
	void utilisateurNonConnecteAppelUrlListeUtilisateur_403Attendu() throws Exception {
		mvc.perform(get("/utilisateur")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = {"UTILISATEUR"})
	void utilisateurConnecteAppelUrlListeUtilisateur_OKAttendu() throws Exception {
		mvc.perform(get("/utilisateur")).andExpect(status().isOk());
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
	@WithMockUser(roles = {"ADMINISTRATEUR"})
	void administrateurAppelUrlListeBonMateriel_OKAttendu() throws Exception {
		mvc.perform(get("/liste-bonmateriel")).andExpect(status().isOk());
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

		// Vérifiez le résultat en fonction de vos attentes
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

	@Test
	@WithMockUser(roles = {"ADMINISTRATEUR"})
	void testVerificationEmailExistanteImportUtilisateurs() throws Exception {
		// Créez un fichier de test en mémoire avec les données
		Workbook classeur = new XSSFWorkbook();
		Sheet feuille1 = classeur.createSheet("Feuille1");
		Row ligne1 = feuille1.createRow(0);
		Cell cellule2 = ligne1.createCell(0);
		cellule2.setCellValue("John");
		Cell cellule3 = ligne1.createCell(1);
		cellule3.setCellValue("56789");
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
		cellule8.setCellValue("john@example.com");
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
				.andExpect(status().isBadRequest())
				.andReturn();


	}


}


