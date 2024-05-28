package src;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class LectureBD {
   private Connection conn;

   public class Role {
      public Role(int i, String n, String p) {
         id = i;
         nom = n;
         personnage = p;
      }

      protected int id;
      protected String nom;
      protected String personnage;
   }

   public LectureBD() {
      connectionBD();
   }

   public void lecturePersonnes(String nomFichier) {
      try {
         XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
         XmlPullParser parser = factory.newPullParser();

         InputStream is = new FileInputStream(nomFichier);
         parser.setInput(is, null);

         int eventType = parser.getEventType();

         String tag = null,
               nom = null,
               anniversaire = null,
               lieu = null,
               photo = null,
               bio = null;

         int id = -1;

         while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
               tag = parser.getName();

               if (tag.equals("personne") && parser.getAttributeCount() == 1)
                  id = Integer.parseInt(parser.getAttributeValue(0));
            } else if (eventType == XmlPullParser.END_TAG) {
               tag = null;

               if (parser.getName().equals("personne") && id >= 0) {
                  insertionPersonne(id, nom, anniversaire, lieu, photo, bio);

                  id = -1;
                  nom = null;
                  anniversaire = null;
                  lieu = null;
                  photo = null;
                  bio = null;
               }
            } else if (eventType == XmlPullParser.TEXT && id >= 0) {
               if (tag != null) {
                  if (tag.equals("nom"))
                     nom = parser.getText();
                  else if (tag.equals("anniversaire"))
                     anniversaire = parser.getText();
                  else if (tag.equals("lieu"))
                     lieu = parser.getText();
                  else if (tag.equals("photo"))
                     photo = parser.getText();
                  else if (tag.equals("bio"))
                     bio = parser.getText();
               }
            }

            eventType = parser.next();
         }
      } catch (XmlPullParserException e) {
         System.out.println(e);
      } catch (IOException e) {
         System.out.println("IOException while parsing " + nomFichier);
      }
   }

   public void lectureFilms(String nomFichier) {
      try {
         XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
         XmlPullParser parser = factory.newPullParser();

         InputStream is = new FileInputStream(nomFichier);
         parser.setInput(is, null);

         int eventType = parser.getEventType();

         String tag = null,
               titre = null,
               langue = null,
               poster = null,
               roleNom = null,
               rolePersonnage = null,
               realisateurNom = null,
               resume = null;

         ArrayList<String> pays = new ArrayList<String>();
         ArrayList<String> genres = new ArrayList<String>();
         ArrayList<String> scenaristes = new ArrayList<String>();
         ArrayList<Role> roles = new ArrayList<Role>();
         ArrayList<String> annonces = new ArrayList<String>();

         int id = -1,
               annee = -1,
               duree = -1,
               roleId = -1,
               realisateurId = -1;

         while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
               tag = parser.getName();

               if (tag.equals("film") && parser.getAttributeCount() == 1)
                  id = Integer.parseInt(parser.getAttributeValue(0));
               else if (tag.equals("realisateur") && parser.getAttributeCount() == 1)
                  realisateurId = Integer.parseInt(parser.getAttributeValue(0));
               else if (tag.equals("acteur") && parser.getAttributeCount() == 1)
                  roleId = Integer.parseInt(parser.getAttributeValue(0));
            } else if (eventType == XmlPullParser.END_TAG) {
               tag = null;

               if (parser.getName().equals("film") && id >= 0) {
                  insertionFilm(id, titre, annee, pays, langue,
                        duree, resume, genres, realisateurNom,
                        realisateurId, scenaristes,
                        roles, poster, annonces);

                  id = -1;
                  annee = -1;
                  duree = -1;
                  titre = null;
                  langue = null;
                  poster = null;
                  resume = null;
                  realisateurNom = null;
                  roleNom = null;
                  rolePersonnage = null;
                  realisateurId = -1;
                  roleId = -1;

                  genres.clear();
                  scenaristes.clear();
                  roles.clear();
                  annonces.clear();
                  pays.clear();
               }
               if (parser.getName().equals("role") && roleId >= 0) {
                  roles.add(new Role(roleId, roleNom, rolePersonnage));
                  roleId = -1;
                  roleNom = null;
                  rolePersonnage = null;
               }
            } else if (eventType == XmlPullParser.TEXT && id >= 0) {
               if (tag != null) {
                  if (tag.equals("titre"))
                     titre = parser.getText();
                  else if (tag.equals("annee"))
                     annee = Integer.parseInt(parser.getText());
                  else if (tag.equals("pays"))
                     pays.add(parser.getText());
                  else if (tag.equals("langue"))
                     langue = parser.getText();
                  else if (tag.equals("duree"))
                     duree = Integer.parseInt(parser.getText());
                  else if (tag.equals("resume"))
                     resume = parser.getText();
                  else if (tag.equals("genre"))
                     genres.add(parser.getText());
                  else if (tag.equals("realisateur"))
                     realisateurNom = parser.getText();
                  else if (tag.equals("scenariste"))
                     scenaristes.add(parser.getText());
                  else if (tag.equals("acteur"))
                     roleNom = parser.getText();
                  else if (tag.equals("personnage"))
                     rolePersonnage = parser.getText();
                  else if (tag.equals("poster"))
                     poster = parser.getText();
                  else if (tag.equals("annonce"))
                     annonces.add(parser.getText());
               }
            }

            eventType = parser.next();
         }
      } catch (XmlPullParserException e) {
         System.out.println(e);
      } catch (IOException e) {
         System.out.println("IOException while parsing " + nomFichier);
      }
   }

   public void lectureClients(String nomFichier) {
      try {
         XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
         XmlPullParser parser = factory.newPullParser();

         InputStream is = new FileInputStream(nomFichier);
         parser.setInput(is, null);

         int eventType = parser.getEventType();

         String tag = null,
               nomFamille = null,
               prenom = null,
               courriel = null,
               tel = null,
               anniv = null,
               adresse = null,
               ville = null,
               province = null,
               codePostal = null,
               carte = null,
               noCarte = null,
               motDePasse = null,
               forfait = null;

         int id = -1,
               expMois = -1,
               expAnnee = -1;

         while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
               tag = parser.getName();

               if (tag.equals("client") && parser.getAttributeCount() == 1)
                  id = Integer.parseInt(parser.getAttributeValue(0));
            } else if (eventType == XmlPullParser.END_TAG) {
               tag = null;

               if (parser.getName().equals("client") && id >= 0) {
                  insertionClient(id, nomFamille, prenom, courriel, tel,
                        anniv, adresse, ville, province,
                        codePostal, carte, noCarte,
                        expMois, expAnnee, motDePasse, forfait);

                  nomFamille = null;
                  prenom = null;
                  courriel = null;
                  tel = null;
                  anniv = null;
                  adresse = null;
                  ville = null;
                  province = null;
                  codePostal = null;
                  carte = null;
                  noCarte = null;
                  motDePasse = null;
                  forfait = null;

                  id = -1;
                  expMois = -1;
                  expAnnee = -1;
               }
            } else if (eventType == XmlPullParser.TEXT && id >= 0) {
               if (tag != null) {
                  if (tag.equals("nom-famille"))
                     nomFamille = parser.getText();
                  else if (tag.equals("prenom"))
                     prenom = parser.getText();
                  else if (tag.equals("courriel"))
                     courriel = parser.getText();
                  else if (tag.equals("tel"))
                     tel = parser.getText();
                  else if (tag.equals("anniversaire"))
                     anniv = parser.getText();
                  else if (tag.equals("adresse"))
                     adresse = parser.getText();
                  else if (tag.equals("ville"))
                     ville = parser.getText();
                  else if (tag.equals("province"))
                     province = parser.getText();
                  else if (tag.equals("code-postal"))
                     codePostal = parser.getText();
                  else if (tag.equals("carte"))
                     carte = parser.getText();
                  else if (tag.equals("no"))
                     noCarte = parser.getText();
                  else if (tag.equals("exp-mois"))
                     expMois = Integer.parseInt(parser.getText());
                  else if (tag.equals("exp-annee"))
                     expAnnee = Integer.parseInt(parser.getText());
                  else if (tag.equals("mot-de-passe"))
                     motDePasse = parser.getText();
                  else if (tag.equals("forfait"))
                     forfait = parser.getText();
               }
            }

            eventType = parser.next();
         }
      } catch (XmlPullParserException e) {
         System.out.println(e);
      } catch (IOException e) {
         System.out.println("IOException while parsing " + nomFichier);
      }
   }

   private void insertionPersonne(int id, String nom, String anniv, String lieu, String photo, String bio) {
      try {
         String sql = "INSERT INTO Personne (noPersonne, nom, dateNaissance, lieuNaissance, photo, biographie) VALUES (?, ?, ?, ?, ?, ?)";
         PreparedStatement pstmt = conn.prepareStatement(sql);
         pstmt.setInt(1, id);
         pstmt.setString(2, nom);
         pstmt.setDate(3, Date.valueOf(anniv));
         pstmt.setString(4, lieu);
         pstmt.setString(5, photo);

         // Tronquer la biographie à 255 caractères si nécessaire
         if (bio.length() > 255) {
            bio = bio.substring(0, 255);
         }
         pstmt.setString(6, bio);

         pstmt.executeUpdate();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   private void insertionFilm(int id, String titre, int annee,
         ArrayList<String> pays, String langue, int duree, String resume,
         ArrayList<String> genres, String realisateurNom, int realisateurId,
         ArrayList<String> scenaristes,
         ArrayList<Role> roles, String poster,
         ArrayList<String> annonces) {
      try {
         String sql = "INSERT INTO Film (noFilm, nomFilm, dateSortie, paysProduction, langue, duree, resume, genre, qteDisponible) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
         PreparedStatement pstmt = conn.prepareStatement(sql);
         pstmt.setInt(1, id);
         pstmt.setString(2, titre);
         pstmt.setInt(3, annee);
         pstmt.setString(4, String.join(",", pays));
         pstmt.setString(5, langue);
         pstmt.setInt(6, duree);
         pstmt.setString(7, resume);
         pstmt.setString(8, String.join(",", genres));
         pstmt.setInt(9, (int) (Math.random() * 100) + 1);
         pstmt.executeUpdate();

         // Insertion des autres informations liées au film (réalisateurs, scénaristes,
         // rôles, etc.)
         for (String scenariste : scenaristes) {
            sql = "INSERT INTO Scenariste (noFilm, nomScenariste) VALUES (?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.setString(2, scenariste);
            pstmt.executeUpdate();
         }

         for (Role role : roles) {
            sql = "INSERT INTO Role (noFilm, noPersonne, nomRole, personnage) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.setInt(2, role.id);
            pstmt.setString(3, role.nom);
            pstmt.setString(4, role.personnage);
            pstmt.executeUpdate();
         }

         // Insérez les annonces et autres informations supplémentaires ici si nécessaire

      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   private void insertionClient(int id, String nomFamille, String prenom,
         String courriel, String tel, String anniv,
         String adresse, String ville, String province,
         String codePostal, String carte, String noCarte,
         int expMois, int expAnnee, String motDePasse,
         String forfait) {
      try {
         String sql = "INSERT INTO Client (noClient, nomFamille, prenom, courriel, noTelephone, dateNaissance, adresse, ville, province, codePostal, carte, noCarte, expMois, expAnnee, motDePasse, forfait) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
         PreparedStatement pstmt = conn.prepareStatement(sql);
         pstmt.setInt(1, id);
         pstmt.setString(2, nomFamille);
         pstmt.setString(3, prenom);
         pstmt.setString(4, courriel);
         pstmt.setString(5, tel);
         pstmt.setDate(6, Date.valueOf(anniv));
         pstmt.setString(7, adresse);
         pstmt.setString(8, ville);
         pstmt.setString(9, province);
         pstmt.setString(10, codePostal);
         pstmt.setString(11, carte);
         pstmt.setString(12, noCarte);
         pstmt.setInt(13, expMois);
         pstmt.setInt(14, expAnnee);
         pstmt.setString(15, motDePasse);
         pstmt.setString(16, forfait);
         pstmt.executeUpdate();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   private void connectionBD() {
      try {
         // Charger explicitement le pilote JDBC Oracle
         Class.forName("oracle.jdbc.driver.OracleDriver");

         // Informations de connexion JDBC
         String url = "jdbc:oracle:thin:@//bdlog660.ens.ad.etsmtl.ca:1521/ORCLPDB.ens.ad.etsmtl.ca";
         String user = "EQUIPE111"; // Remplacez par votre nom d'utilisateur
         String password = "iVkGt1il"; // Remplacez par votre mot de passe

         // Afficher les informations de connexion pour vérification
         System.out.println("URL de connexion: " + url);
         System.out.println("Nom d'utilisateur: " + user);

         conn = DriverManager.getConnection(url, user, password);

         // Vérifier si la connexion est réussie
         if (conn != null) {
            System.out.println("Connexion réussie !");
         } else {
            System.out.println("Connexion échouée !");
         }
      } catch (SQLException e) {
         e.printStackTrace();
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }
   }

   public static void main(String[] args) {
      if (args.length < 3) {
         System.out.println("Usage: java LectureBD <path_to_personnes_xml> <path_to_films_xml> <path_to_clients_xml>");
         return;
      }

      LectureBD lecture = new LectureBD();

      lecture.lecturePersonnes(args[0]);
      lecture.lectureFilms(args[1]);
      lecture.lectureClients(args[2]);
   }

}
