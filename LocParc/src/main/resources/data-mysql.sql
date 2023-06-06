INSERT INTO `role` (`id`, `nom`)
VALUES
(1, 'ROLE_ADMINISTRATEUR'),
(2,'ROLE_GESTIONNAIRE'),
(3, 'ROLE_UTILISATEUR');

INSERT INTO `utilisateur`(`id`, `date_creation`, `date_miseajour`, `email`, `mot_de_passe`, `nom`, `prenom`,`nom_image_profil`,`telephone`,`role_id`)
VALUES
(1,UTC_TIMESTAMP(),UTC_TIMESTAMP(),'ab@c.com','$2a$10$wXW2wHA2bu1TdQ26p.2UoehWv8m92w88kabSeL.348VqkpWvSt51q','PATATE','Chips',"utilisateur.jpg",'0698765432',1),
(2,UTC_TIMESTAMP(),UTC_TIMESTAMP(),'ab@y.com','$2a$10$wXW2wHA2bu1TdQ26p.2UoehWv8m92w88kabSeL.348VqkpWvSt51q','JOYEUX','Kevin',"utilisateur.jpg",'0698765430',2),
(3,UTC_TIMESTAMP(),UTC_TIMESTAMP(),'ab@z.com','$2a$10$wXW2wHA2bu1TdQ26p.2UoehWv8m92w88kabSeL.348VqkpWvSt51q','ADAM','Damien',"utilisateur.jpg",'0698765439',1),
(4,UTC_TIMESTAMP(),UTC_TIMESTAMP(),'ab@x.com','$2a$10$wXW2wHA2bu1TdQ26p.2UoehWv8m92w88kabSeL.348VqkpWvSt51q','TRICHOT','Robin',Null,'0698765438',2),
(5,UTC_TIMESTAMP(),UTC_TIMESTAMP(),'ab@w.com','$2a$10$wXW2wHA2bu1TdQ26p.2UoehWv8m92w88kabSeL.348VqkpWvSt51q','CASTELLI','Luigi',Null,'0698765437',2),
(6,UTC_TIMESTAMP(),UTC_TIMESTAMP(),'ab@a.com','$2a$10$wXW2wHA2bu1TdQ26p.2UoehWv8m92w88kabSeL.348VqkpWvSt51q','John','DOE',Null,'065412345',1),
(7,UTC_TIMESTAMP(),UTC_TIMESTAMP(),'zz@z.com','$2a$10$wXW2wHA2bu1TdQ26p.2UoehWv8m92w88kabSeL.348VqkpWvSt51q','Jack','Sparrow',Null,'065412345',3);


INSERT INTO `type`(`id`, `nom`)
VALUES
(1,'WEBCAM'),
(2,'CASQUE_VR'),
(3,'ORDINATEUR_PORTABLE'),
(4,'VIDEOPROJECTEUR');

INSERT INTO `etat`(`id`, `nom`)
VALUES
(1,'TRES BON'),
(2,'BON'),
(3,'CORRECT'),
(4,'MAUVAIS'),
(5,'HORS SERVICE'),
(6,'EN REPARATION');

INSERT INTO `statut`(`id`, `nom`)
VALUES
(1,'disponible'),
(2,'indisponible');


INSERT INTO `materiel`(`id`, `modele`, `reference`,`nom_image_materiel` ,`type_id`, `etat_id` , `statut_id`,`marque`)
VALUES
(1,'ACER','ACER x900',"webcamexample.jpg",1,1,1,'ACER'),
(2,'ACER','ACER x1900',"ordiportlenovoexample.jpg",3,6,2,'ACER'),
(3,'Corsaire','Corsaire Y900',"casquevrexemple.jpg",2,2,1,'Corsaire'),
(4,'Projector','Projo 9000',Null,4,3,1,'Projector'),
(5,'Projector','ProX 9500',Null,4,4,2,'Projector'),
(6,'ACER','ACER x1900',"ordiportlenovoexample.jpg",3,1,1,'lenovo'),
(7,'ACER','ACER x1900',"ordiportlenovoexample.jpg",3,1,1,'lenovo'),
(8,'ACER','ACER x1900',"ordiportlenovoexample.jpg",3,1,1,'lenovo');



INSERT INTO `pret`(`id`, `commentaire`, `date_debut`, `date_fin`, `motif`, `materiel_id`, `utilisateur_id`)
VALUES
(1, "Materiel en bon etat", UTC_TIMESTAMP(), DATE_ADD(UTC_TIMESTAMP(), INTERVAL 10 DAY), "Besoin personel", 3, 1),
(2, "Materiel correct", UTC_TIMESTAMP(), DATE_ADD(UTC_TIMESTAMP(), INTERVAL 15 DAY), "Besoin personel", 4, 2),
(3, "Materiel en mauvais etat", UTC_TIMESTAMP(), DATE_ADD(UTC_TIMESTAMP(), INTERVAL 8 DAY), "Besoin personel", 5, 3);

INSERT INTO `cause`(`id`, `nom`)
VALUES
(1,'VOL'),
(2,'PERTE'),
(3,'DEGAT'),
(4,'PANNE'),
(5,'INCONNU');

INSERT INTO `declaration`(`etat_id`, `materiel_id`, `pret_id`, `cause_id`, `date_declaration`, `date_retour_anticipe`)
VALUES
 (1,3,1,4,UTC_TIMESTAMP(),UTC_TIMESTAMP()),
 (2,1,2,3,UTC_TIMESTAMP(),UTC_TIMESTAMP()),
 (3,5,2,2,UTC_TIMESTAMP(),UTC_TIMESTAMP());






