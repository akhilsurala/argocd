//package com.sunseed.repository;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import java.time.Instant;
//import java.util.List;
//import java.util.Optional;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.annotation.DirtiesContext.ClassMode;
//import org.springframework.test.context.ContextConfiguration;
//
//import com.sunseed.SunseedAgriPVApplication;
//import com.sunseed.entity.Projects;
//import com.sunseed.entity.Runs;
//import com.sunseed.entity.UserProfile;
//import com.sunseed.enums.CommonStatus;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = Replace.NONE)
//@ContextConfiguration(classes = SunseedAgriPVApplication.class)
//@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
//public class ProjectsRepositoryTests {
//
//	@Autowired
//	private ProjectsRepository projectsRepository;
//
//	@Autowired
//	private TestEntityManager entityManager;
//
//	private static UserProfile user1;
//	private static UserProfile user2;
//	private static Projects projectAlpha;
//	private static Projects projectBeta;
//	private static Runs run1;
//	private static Runs run2;
//	private static Runs run3;
//
//	@BeforeEach
//	void setUp() {
//
//		user1 = UserProfile.builder().firstName("John").lastName("Doe").emailId("john.doe@example.com")
//				.phoneNumber("1234567890").createdAt(Instant.now()).updatedAt(Instant.now()).build();
//		entityManager.persist(user1);
//
//		user2 = UserProfile.builder().firstName("Jane").lastName("Doe").emailId("jane.doe@example.com")
//				.phoneNumber("0987654321").createdAt(Instant.now()).updatedAt(Instant.now()).build();
//		entityManager.persist(user2);
//
//		projectAlpha = Projects.builder().projectName("Project Alpha").userProfile(user1).latitude("34.0522")
//				.longitude("-118.2437").projectStatus(CommonStatus.ACTIVE).area(1.0)
//				.polygonCoordinates("[(34.0522, -118.2437)]").comments("Initial project").createdAt(Instant.now())
//				.updatedAt(Instant.now()).build();
//		entityManager.persist(projectAlpha);
//
//		projectBeta = Projects.builder().projectName("Project Beta").userProfile(user2).latitude("40.7128")
//				.longitude("-74.0060").projectStatus(CommonStatus.ACTIVE).area(2.0)
//				.polygonCoordinates("[(40.7128, -74.0060)]").comments("Second project").createdAt(Instant.now())
//				.updatedAt(Instant.now()).build();
//		entityManager.persist(projectBeta);
//
//		run1 = Runs.builder().inProject(projectAlpha).runName("Run 1").createdAt(Instant.now()).updatedAt(Instant.now())
//				.build();
//		entityManager.persist(run1);
//
//		run2 = Runs.builder().inProject(projectAlpha).runName("Run 2").createdAt(Instant.now()).updatedAt(Instant.now())
//				.build();
//		entityManager.persist(run2);
//
//		run3 = Runs.builder().inProject(projectBeta).runName("Run 3").createdAt(Instant.now()).updatedAt(Instant.now())
//				.build();
//		entityManager.persist(run3);
//
//		entityManager.flush();
//	}
//
//	@Test
//	void testExistsByUserProfileAndProjectName() {
//		Optional<Projects> project = projectsRepository.existsByUserProfileAndProjectName(1L, "Project Alpha");
//		assertThat(project).isPresent();
//
//		project = projectsRepository.existsByUserProfileAndProjectName(1L, "Project Beta");
//		assertThat(project).isNotPresent();
//
//		project = projectsRepository.existsByUserProfileAndProjectName(1L, "PROJECT ALPHA");
//		assertThat(project).isPresent();
//
//		project = projectsRepository.existsByUserProfileAndProjectName(1L, "project alpha");
//		assertThat(project).isPresent();
//	}
//
//	@Test
//	void testCountRunsByProjectId() {
//		long runCount = projectsRepository.countRunsByProjectId(1L);
//		assertThat(runCount).isEqualTo(2);
//
//		runCount = projectsRepository.countRunsByProjectId(2L);
//		assertThat(runCount).isEqualTo(1);
//
//		runCount = projectsRepository.countRunsByProjectId(3L);
//		assertThat(runCount).isEqualTo(0);
//	}
//
//	@Test
//	void testFindByUserProfileUserProfileId() {
//		List<Projects> projects = projectsRepository.findByUserProfileUserProfileId(1L);
//		assertThat(projects).hasSize(1);
//		assertThat(projects.get(0).getProjectName()).isEqualTo("Project Alpha");
//
//		projects = projectsRepository.findByUserProfileUserProfileId(2L);
//		assertThat(projects).hasSize(1);
//		assertThat(projects.get(0).getProjectName()).isEqualTo("Project Beta");
//
//		projects = projectsRepository.findByUserProfileUserProfileId(3L);
//		assertThat(projects).hasSize(0);
//	}
//}
