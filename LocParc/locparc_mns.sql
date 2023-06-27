-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : mer. 21 juin 2023 à 08:46
-- Version du serveur : 8.0.31
-- Version de PHP : 8.0.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `locparc_mns`
--

-- --------------------------------------------------------

--
-- Structure de la table `cause`
--

DROP TABLE IF EXISTS `cause`;
CREATE TABLE IF NOT EXISTS `cause` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `cause`
--

INSERT INTO `cause` (`id`, `nom`) VALUES
(1, 'VOL'),
(2, 'PERTE'),
(3, 'DEGAT'),
(4, 'PANNE'),
(5, 'INCONNU');

-- --------------------------------------------------------

--
-- Structure de la table `declaration`
--

DROP TABLE IF EXISTS `declaration`;
CREATE TABLE IF NOT EXISTS `declaration` (
  `cause_id` int NOT NULL,
  `etat_id` int NOT NULL,
  `materiel_id` int NOT NULL,
  `pret_id` int NOT NULL,
  `date_declaration` date DEFAULT NULL,
  `date_retour_anticipe` date DEFAULT NULL,
  PRIMARY KEY (`cause_id`,`etat_id`,`materiel_id`,`pret_id`),
  KEY `FK1y1s7faftp5fkcugqtiyaiq4b` (`etat_id`),
  KEY `FK5e8rsmg4gr842ddlt81j2j19i` (`materiel_id`),
  KEY `FKmqa12n0yxoq8sjoojtvpa5pki` (`pret_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `declaration`
--

INSERT INTO `declaration` (`cause_id`, `etat_id`, `materiel_id`, `pret_id`, `date_declaration`, `date_retour_anticipe`) VALUES
(2, 3, 5, 2, '2023-06-20', '2023-06-20'),
(3, 2, 1, 2, '2023-06-20', '2023-06-20'),
(4, 1, 3, 1, '2023-06-20', '2023-06-20');

-- --------------------------------------------------------

--
-- Structure de la table `etat`
--

DROP TABLE IF EXISTS `etat`;
CREATE TABLE IF NOT EXISTS `etat` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `etat`
--

INSERT INTO `etat` (`id`, `nom`) VALUES
(1, 'TRES BON'),
(2, 'BON'),
(3, 'CORRECT'),
(4, 'MAUVAIS'),
(5, 'HORS SERVICE'),
(6, 'EN REPARATION');

-- --------------------------------------------------------

--
-- Structure de la table `hibernate_sequence`
--

DROP TABLE IF EXISTS `hibernate_sequence`;
CREATE TABLE IF NOT EXISTS `hibernate_sequence` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `hibernate_sequence`
--

INSERT INTO `hibernate_sequence` (`next_val`) VALUES
(1);

-- --------------------------------------------------------

--
-- Structure de la table `materiel`
--

DROP TABLE IF EXISTS `materiel`;
CREATE TABLE IF NOT EXISTS `materiel` (
  `id` int NOT NULL AUTO_INCREMENT,
  `date_debut_emprunt` datetime(6) DEFAULT NULL,
  `date_fin_emprunt` datetime(6) DEFAULT NULL,
  `marque` varchar(50) NOT NULL,
  `modele` varchar(50) NOT NULL,
  `nom_image_materiel` varchar(255) DEFAULT NULL,
  `reference` varchar(50) NOT NULL,
  `etat_id` int DEFAULT NULL,
  `pret_id` int DEFAULT NULL,
  `statut_id` int DEFAULT NULL,
  `type_id` int DEFAULT NULL,
  `utilisateur_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhpjtf783bawjupveeb1gtwt1k` (`etat_id`),
  KEY `FK87q7phdp8qeuv085ntjbhjx8x` (`pret_id`),
  KEY `FKdw20jjqpmw41funpnono3vwfb` (`statut_id`),
  KEY `FKnqm0ujpetfyejxk3yu34myejb` (`type_id`),
  KEY `FKsuqq7lg33xylwwqfwrbukhi88` (`utilisateur_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `materiel`
--

INSERT INTO `materiel` (`id`, `date_debut_emprunt`, `date_fin_emprunt`, `marque`, `modele`, `nom_image_materiel`, `reference`, `etat_id`, `pret_id`, `statut_id`, `type_id`, `utilisateur_id`) VALUES
(1, NULL, NULL, 'ACER', 'ACER', 'webcamexample.jpg', 'ACER x900', 1, NULL, 1, 1, NULL),
(2, NULL, NULL, 'ACER', 'ACER', 'ordiportlenovoexample.jpg', 'ACER x1900', 6, NULL, 2, 3, NULL),
(3, NULL, NULL, 'Corsaire', 'Corsaire', 'casquevrexemple.jpg', 'Corsaire Y900', 2, NULL, 1, 2, NULL),
(4, NULL, NULL, 'Projector', 'Projector', NULL, 'Projo 9000', 3, NULL, 1, 4, NULL),
(5, NULL, NULL, 'Projector', 'Projector', NULL, 'ProX 9500', 4, NULL, 2, 4, NULL),
(6, NULL, NULL, 'lenovo', 'ACER', 'ordiportlenovoexample.jpg', 'ACER x1900', 1, NULL, 1, 3, NULL),
(7, NULL, NULL, 'lenovo', 'ACER', 'ordiportlenovoexample.jpg', 'ACER x1900', 1, NULL, 1, 3, NULL),
(8, NULL, NULL, 'lenovo', 'ACER', 'ordiportlenovoexample.jpg', 'ACER x1900', 1, NULL, 1, 3, NULL);

-- --------------------------------------------------------

--
-- Structure de la table `pret`
--

DROP TABLE IF EXISTS `pret`;
CREATE TABLE IF NOT EXISTS `pret` (
  `id` int NOT NULL AUTO_INCREMENT,
  `commentaire` varchar(255) DEFAULT NULL,
  `date_debut` datetime(6) DEFAULT NULL,
  `date_fin` datetime(6) DEFAULT NULL,
  `motif` varchar(255) DEFAULT NULL,
  `reference_pret` varchar(255) DEFAULT NULL,
  `materiel_id` int DEFAULT NULL,
  `statut_id` int DEFAULT NULL,
  `utilisateur_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKnhkexbwubbjia1t61ao3y189n` (`materiel_id`),
  KEY `FKrbbbegkxh6c9wnybu9phurbs3` (`statut_id`),
  KEY `FKcpi972agyjsxlxf6hie4sstuu` (`utilisateur_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `pret`
--

INSERT INTO `pret` (`id`, `commentaire`, `date_debut`, `date_fin`, `motif`, `reference_pret`, `materiel_id`, `statut_id`, `utilisateur_id`) VALUES
(1, 'Materiel en bon etat', '2023-06-20 13:38:00.000000', '2023-06-30 13:38:00.000000', 'Besoin personel', NULL, 3, NULL, 1),
(2, 'Materiel correct', '2023-06-20 13:38:00.000000', '2023-07-05 13:38:00.000000', 'Besoin personel', NULL, 4, NULL, 2),
(3, 'Materiel en mauvais etat', '2023-06-20 13:38:00.000000', '2023-06-28 13:38:00.000000', 'Besoin personel', NULL, 5, NULL, 3);

-- --------------------------------------------------------

--
-- Structure de la table `role`
--

DROP TABLE IF EXISTS `role`;
CREATE TABLE IF NOT EXISTS `role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(40) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `role`
--

INSERT INTO `role` (`id`, `nom`) VALUES
(1, 'ROLE_ADMINISTRATEUR'),
(2, 'ROLE_GESTIONNAIRE'),
(3, 'ROLE_UTILISATEUR');

-- --------------------------------------------------------

--
-- Structure de la table `statut`
--

DROP TABLE IF EXISTS `statut`;
CREATE TABLE IF NOT EXISTS `statut` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `statut`
--

INSERT INTO `statut` (`id`, `nom`) VALUES
(1, 'disponible'),
(2, 'indisponible');

-- --------------------------------------------------------

--
-- Structure de la table `suivi_pret`
--

DROP TABLE IF EXISTS `suivi_pret`;
CREATE TABLE IF NOT EXISTS `suivi_pret` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `type`
--

DROP TABLE IF EXISTS `type`;
CREATE TABLE IF NOT EXISTS `type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `type`
--

INSERT INTO `type` (`id`, `nom`) VALUES
(1, 'WEBCAM'),
(2, 'CASQUE_VR'),
(3, 'ORDINATEUR_PORTABLE'),
(4, 'VIDEOPROJECTEUR');

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

DROP TABLE IF EXISTS `utilisateur`;
CREATE TABLE IF NOT EXISTS `utilisateur` (
  `id` int NOT NULL AUTO_INCREMENT,
  `date_creation` date DEFAULT NULL,
  `date_miseajour` datetime(6) DEFAULT NULL,
  `email` varchar(50) NOT NULL,
  `mot_de_passe` varchar(255) DEFAULT NULL,
  `nom` varchar(50) NOT NULL,
  `nom_image_profil` varchar(255) DEFAULT NULL,
  `prenom` varchar(50) NOT NULL,
  `telephone` varchar(20) NOT NULL,
  `pret_id` int DEFAULT NULL,
  `role_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKe95u89kxcmh01s6b4lfy4lj7a` (`pret_id`),
  KEY `FKaqe8xtajee4k0wlqrvh2pso4l` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `utilisateur`
--

INSERT INTO `utilisateur` (`id`, `date_creation`, `date_miseajour`, `email`, `mot_de_passe`, `nom`, `nom_image_profil`, `prenom`, `telephone`, `pret_id`, `role_id`) VALUES
(1, '2023-06-20', '2023-06-20 13:38:00.000000', 'ab@c.com', '$2a$10$wXW2wHA2bu1TdQ26p.2UoehWv8m92w88kabSeL.348VqkpWvSt51q', 'PATATE', 'utilisateur.jpg', 'Chips', '0698765432', NULL, 1),
(2, '2023-06-20', '2023-06-20 13:38:00.000000', 'ab@y.com', '$2a$10$wXW2wHA2bu1TdQ26p.2UoehWv8m92w88kabSeL.348VqkpWvSt51q', 'JOYEUX', 'utilisateur.jpg', 'Kevin', '0698765430', NULL, 2),
(3, '2023-06-20', '2023-06-20 13:38:00.000000', 'ab@z.com', '$2a$10$wXW2wHA2bu1TdQ26p.2UoehWv8m92w88kabSeL.348VqkpWvSt51q', 'ADAM', 'utilisateur.jpg', 'Damien', '0698765439', NULL, 1),
(4, '2023-06-20', '2023-06-20 13:38:00.000000', 'ab@x.com', '$2a$10$wXW2wHA2bu1TdQ26p.2UoehWv8m92w88kabSeL.348VqkpWvSt51q', 'TRICHOT', NULL, 'Robin', '0698765438', NULL, 2),
(5, '2023-06-20', '2023-06-20 13:38:00.000000', 'ab@w.com', '$2a$10$wXW2wHA2bu1TdQ26p.2UoehWv8m92w88kabSeL.348VqkpWvSt51q', 'CASTELLI', NULL, 'Luigi', '0698765437', NULL, 2),
(6, '2023-06-20', '2023-06-20 13:38:00.000000', 'ab@a.com', '$2a$10$wXW2wHA2bu1TdQ26p.2UoehWv8m92w88kabSeL.348VqkpWvSt51q', 'John', NULL, 'DOE', '065412345', NULL, 1),
(7, '2023-06-20', '2023-06-20 13:38:00.000000', 'zz@z.com', '$2a$10$wXW2wHA2bu1TdQ26p.2UoehWv8m92w88kabSeL.348VqkpWvSt51q', 'Jack', NULL, 'Sparrow', '065412345', NULL, 3),
(8, '2023-06-20', '2023-06-20 13:38:03.637653', 'john@example.com', '$2a$10$.kDgG6I5GGQIZ0VPwiX/rOPq/MTOcxmbyXP2ZAtZUrO1lm4yjusBK', 'Doe', NULL, 'John', '987654321', NULL, 3),
(9, '2023-06-20', '2023-06-20 13:38:03.804715', 'jane@example.com', '$2a$10$oR6Q4GDbymbWuK0vqv6DW.XWgK3tYGBIS6CtoG8veB4c18s0yS462', 'Doe', NULL, 'Jane', '123456789', NULL, 3);

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `declaration`
--
ALTER TABLE `declaration`
  ADD CONSTRAINT `FK1y1s7faftp5fkcugqtiyaiq4b` FOREIGN KEY (`etat_id`) REFERENCES `etat` (`id`),
  ADD CONSTRAINT `FK5e8rsmg4gr842ddlt81j2j19i` FOREIGN KEY (`materiel_id`) REFERENCES `materiel` (`id`),
  ADD CONSTRAINT `FKm4c6ruhfylac0w2p49r3rgijv` FOREIGN KEY (`cause_id`) REFERENCES `cause` (`id`),
  ADD CONSTRAINT `FKmqa12n0yxoq8sjoojtvpa5pki` FOREIGN KEY (`pret_id`) REFERENCES `pret` (`id`);

--
-- Contraintes pour la table `materiel`
--
ALTER TABLE `materiel`
  ADD CONSTRAINT `FK87q7phdp8qeuv085ntjbhjx8x` FOREIGN KEY (`pret_id`) REFERENCES `pret` (`id`),
  ADD CONSTRAINT `FKdw20jjqpmw41funpnono3vwfb` FOREIGN KEY (`statut_id`) REFERENCES `statut` (`id`),
  ADD CONSTRAINT `FKhpjtf783bawjupveeb1gtwt1k` FOREIGN KEY (`etat_id`) REFERENCES `etat` (`id`),
  ADD CONSTRAINT `FKnqm0ujpetfyejxk3yu34myejb` FOREIGN KEY (`type_id`) REFERENCES `type` (`id`),
  ADD CONSTRAINT `FKsuqq7lg33xylwwqfwrbukhi88` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`);

--
-- Contraintes pour la table `pret`
--
ALTER TABLE `pret`
  ADD CONSTRAINT `FKcpi972agyjsxlxf6hie4sstuu` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`),
  ADD CONSTRAINT `FKnhkexbwubbjia1t61ao3y189n` FOREIGN KEY (`materiel_id`) REFERENCES `materiel` (`id`),
  ADD CONSTRAINT `FKrbbbegkxh6c9wnybu9phurbs3` FOREIGN KEY (`statut_id`) REFERENCES `statut` (`id`);

--
-- Contraintes pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  ADD CONSTRAINT `FKaqe8xtajee4k0wlqrvh2pso4l` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  ADD CONSTRAINT `FKe95u89kxcmh01s6b4lfy4lj7a` FOREIGN KEY (`pret_id`) REFERENCES `pret` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
