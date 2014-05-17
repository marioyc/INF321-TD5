import java.awt.*;
import java.awt.event.*;

import java.util.Random;

class InterfaceB {

	static int largeur = 900;
	static int hauteur = 600;

	static int nSpheresScene = 1000;
	static double facteurRayon = 1.0;
	static double facteurVitesse = 1.0;
	static Sphere[] sphereScene = null;
	static Vecteur3[] vitesseSphereScene = null;
	static double tailleScene = 100.0;

	static Vecteur3[] pointMurScene = null;
	static Vecteur3[] normaleMurScene = null;
	static Vecteur3[] coinScene = null;

	static Vecteur3 OX = new Vecteur3(1.0, 0.0, 0.0);
	static Vecteur3 OY = new Vecteur3(0.0, 1.0, 0.0);
	static Vecteur3 OZ = new Vecteur3(0.0, 0.0, 1.0);

	static boolean sourisCliquee = false;
	static boolean boutonSouris1 = false;
	static boolean boutonSouris2 = false;
	static boolean boutonSouris3 = false;
	static int positionSourisXPrecedent = 0;
	static int positionSourisYPrecedent = 0;

	static double pasSpheres = 90.0;

	static Graphics graphics;
	static Graphics graphics2;
	static int modeVisualisation = 0;
	static int modeCollision = 2;
	static Image offscreen;
	static Panel bas;
	static Random rnd = new Random(0);

	static Camera camera;

	static ListePairesIndices listePairesSpheresEnCollision = null;

	static long tempsCourant = 0;
	static long tempsDeCalcul = 0;
	static boolean simulationActive = false;

	static Graphics ouvreFenetre(int l, int h) {

		Frame fenetre = new Frame("Question 1");
		fenetre.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.out.println("Fenetre fermee, c'est fini.");
				System.exit(0);
			}
		});
		fenetre.setBounds(40, 40, l, h);
		fenetre.setBackground(Color.white);
		fenetre.setForeground(Color.black);
		fenetre.setVisible(true);
		Graphics dessin = fenetre.getGraphics();
		return dessin;

	}

	// fonctions de dessin

	static void dessinerSphere(Graphics g, Sphere s) {

		Vecteur3 PC = new Vecteur3(camera.cible.x - camera.position.x,
				camera.cible.y - camera.position.y, camera.cible.z
						- camera.position.z);
		Vecteur3 ii = new Vecteur3();
		ii.x = PC.y * camera.haut.z - PC.z * camera.haut.y;
		ii.y = PC.z * camera.haut.x - PC.x * camera.haut.z;
		ii.z = PC.x * camera.haut.y - PC.y * camera.haut.x;
		ii.normaliser();

		double PCNorme = PC.norme();
		double PCNorme2 = PC.norme2();

		double Y = PCNorme * Math.tan(Math.toRadians(camera.fovy));
		double X = camera.aspect * Y;

		for (int i = 0; i <= 180; i += pasSpheres) {

			double xPrecedent;
			double yPrecedent;
			double xNouveau = 0.0;
			double yNouveau = 0.0;

			for (int j = 0; j < 360 + pasSpheres; j += pasSpheres) {

				Vecteur3 M = new Vecteur3(s.centre.x + s.rayon
						* Math.sin(Math.toRadians(i))
						* Math.cos(Math.toRadians(j)), s.centre.y + s.rayon
						* Math.cos(Math.toRadians(i)), s.centre.z + s.rayon
						* Math.sin(Math.toRadians(i))
						* Math.sin(Math.toRadians(j)));
				Vecteur3 PM = M.moins(camera.position);

				double PMPC = PM.produitScalaire(PC);
				double lambda = PCNorme2 / PMPC;
				Vecteur3 CM = M.moins(camera.position);

				double a = lambda * (CM.x * ii.x + CM.y * ii.y + CM.z * ii.z);
				double b = lambda
						* (CM.x * camera.haut.x + CM.y * camera.haut.y + CM.z
								* camera.haut.z);

				xPrecedent = xNouveau;
				yPrecedent = yNouveau;

				xNouveau = largeur / 2.0 * (1 + a / X);
				yNouveau = hauteur / 2.0 * (1 - b / Y);

				if (j == 0) {

					xPrecedent = xNouveau;
					yPrecedent = yNouveau;

				}

				g.drawLine((int) xPrecedent, (int) yPrecedent, (int) xNouveau,
						(int) yNouveau);

			}

		}

		for (int j = 0; j <= 360 + pasSpheres; j += pasSpheres) {

			double xPrecedent;
			double yPrecedent;
			double xNouveau = 0.0;
			double yNouveau = 0.0;

			for (int i = 0; i <= 180; i += pasSpheres) {

				Vecteur3 M = new Vecteur3(s.centre.x + s.rayon
						* Math.sin(Math.toRadians(i))
						* Math.cos(Math.toRadians(j)), s.centre.y + s.rayon
						* Math.cos(Math.toRadians(i)), s.centre.z + s.rayon
						* Math.sin(Math.toRadians(i))
						* Math.sin(Math.toRadians(j)));
				Vecteur3 PM = M.moins(camera.position);

				double PMPC = PM.produitScalaire(PC);
				double lambda = PCNorme2 / PMPC;
				Vecteur3 CM = M.moins(camera.position);

				double a = lambda * (CM.x * ii.x + CM.y * ii.y + CM.z * ii.z);
				double b = lambda
						* (CM.x * camera.haut.x + CM.y * camera.haut.y + CM.z
								* camera.haut.z);

				xPrecedent = xNouveau;
				yPrecedent = yNouveau;

				xNouveau = largeur / 2.0 * (1 + a / X);
				yNouveau = hauteur / 2.0 * (1 - b / Y);

				if (i == 0) {

					xPrecedent = xNouveau;
					yPrecedent = yNouveau;

				}

				g.drawLine((int) xPrecedent, (int) yPrecedent, (int) xNouveau,
						(int) yNouveau);

			}

		}

	}

	static void dessinerListePairesIndices(Graphics g, int nSpheres, Sphere s[]) {

		Vecteur3 PC = new Vecteur3(camera.cible.x - camera.position.x,
				camera.cible.y - camera.position.y, camera.cible.z
						- camera.position.z);
		Vecteur3 ii = new Vecteur3();
		ii.x = PC.y * camera.haut.z - PC.z * camera.haut.y;
		ii.y = PC.z * camera.haut.x - PC.x * camera.haut.z;
		ii.z = PC.x * camera.haut.y - PC.y * camera.haut.x;
		ii.normaliser();

		double PCNorme = PC.norme();
		double PCNorme2 = PC.norme2();

		double Y = PCNorme * Math.tan(Math.toRadians(camera.fovy));
		double X = camera.aspect * Y;

		for (int k = 0; k < nSpheres; k++) {

			for (int i = 0; i <= 180; i += pasSpheres) {

				double xPrecedent;
				double yPrecedent;
				double xNouveau = 0.0;
				double yNouveau = 0.0;

				for (int j = 0; j < 360 + pasSpheres; j += pasSpheres) {

					Vecteur3 M = new Vecteur3(s[k].centre.x + s[k].rayon
							* Math.sin(Math.toRadians(i))
							* Math.cos(Math.toRadians(j)), s[k].centre.y
							+ s[k].rayon * Math.cos(Math.toRadians(i)),
							s[k].centre.z + s[k].rayon
									* Math.sin(Math.toRadians(i))
									* Math.sin(Math.toRadians(j)));
					Vecteur3 PM = M.moins(camera.position);

					double PMPC = PM.produitScalaire(PC);
					double lambda = PCNorme2 / PMPC;
					Vecteur3 CM = M.moins(camera.position);

					double a = lambda
							* (CM.x * ii.x + CM.y * ii.y + CM.z * ii.z);
					double b = lambda
							* (CM.x * camera.haut.x + CM.y * camera.haut.y + CM.z
									* camera.haut.z);

					xPrecedent = xNouveau;
					yPrecedent = yNouveau;

					xNouveau = largeur / 2.0 * (1 + a / X);
					yNouveau = hauteur / 2.0 * (1 - b / Y);

					if (j == 0) {

						xPrecedent = xNouveau;
						yPrecedent = yNouveau;

					}

					g.drawLine((int) xPrecedent, (int) yPrecedent,
							(int) xNouveau, (int) yNouveau);

				}

			}

			for (int j = 0; j <= 360 + pasSpheres; j += pasSpheres) {

				double xPrecedent;
				double yPrecedent;
				double xNouveau = 0.0;
				double yNouveau = 0.0;

				for (int i = 0; i <= 180; i += pasSpheres) {

					Vecteur3 M = new Vecteur3(s[k].centre.x + s[k].rayon
							* Math.sin(Math.toRadians(i))
							* Math.cos(Math.toRadians(j)), s[k].centre.y
							+ s[k].rayon * Math.cos(Math.toRadians(i)),
							s[k].centre.z + s[k].rayon
									* Math.sin(Math.toRadians(i))
									* Math.sin(Math.toRadians(j)));
					Vecteur3 PM = M.moins(camera.position);

					double PMPC = PM.produitScalaire(PC);
					double lambda = PCNorme2 / PMPC;
					Vecteur3 CM = M.moins(camera.position);

					double a = lambda
							* (CM.x * ii.x + CM.y * ii.y + CM.z * ii.z);
					double b = lambda
							* (CM.x * camera.haut.x + CM.y * camera.haut.y + CM.z
									* camera.haut.z);

					xPrecedent = xNouveau;
					yPrecedent = yNouveau;

					xNouveau = largeur / 2.0 * (1 + a / X);
					yNouveau = hauteur / 2.0 * (1 - b / Y);

					if (i == 0) {

						xPrecedent = xNouveau;
						yPrecedent = yNouveau;

					}

					g.drawLine((int) xPrecedent, (int) yPrecedent,
							(int) xNouveau, (int) yNouveau);

				}

			}

		}

	}

	static void dessinerSol(Graphics g) {

		Vecteur3 PC = camera.cible.moins(camera.position);
		Vecteur3 ii = new Vecteur3();
		Vecteur3 CM = new Vecteur3();
		ii.x = PC.y * camera.haut.z - PC.z * camera.haut.y;
		ii.y = PC.z * camera.haut.x - PC.x * camera.haut.z;
		ii.z = PC.x * camera.haut.y - PC.y * camera.haut.x;
		ii.normaliser();

		double PCNorme = PC.norme();
		double PCNorme2 = PC.norme2();

		double Y = PCNorme * Math.tan(Math.toRadians(camera.fovy));
		double X = camera.aspect * Y;
		double PMPC;
		double lambda;

		for (double i = -tailleScene; i <= tailleScene + 0.1; i += tailleScene / 8.0) {

			Vecteur3 pointA = OX.fois(i).plus(OZ.fois(-tailleScene)).plus(
					OY.fois(-tailleScene));
			PMPC = pointA.moins(camera.position).produitScalaire(PC);
			lambda = PCNorme2 / PMPC;
			CM = pointA.moins(camera.cible);

			double aA = lambda * CM.produitScalaire(ii);
			double bA = lambda * CM.produitScalaire(camera.haut);

			int xA = (int) (largeur / 2.0 * (1 + aA / X));
			int yA = (int) (hauteur / 2.0 * (1 - bA / Y));

			Vecteur3 pointB = OX.fois(i).plus(OZ.fois(tailleScene)).plus(
					OY.fois(-tailleScene));
			PMPC = pointB.moins(camera.position).produitScalaire(PC);
			lambda = PCNorme2 / PMPC;
			CM = pointB.moins(camera.cible);

			double aB = lambda * CM.produitScalaire(ii);
			double bB = lambda * CM.produitScalaire(camera.haut);

			int xB = (int) (largeur / 2.0 * (1 + aB / X));
			int yB = (int) (hauteur / 2.0 * (1 - bB / Y));

			g.drawLine(xA, yA, xB, yB);

		}

		for (double i = -tailleScene; i <= tailleScene + 0.1; i += tailleScene / 8.0) {

			Vecteur3 pointA = OX.fois(-tailleScene).plus(OZ.fois(i)).plus(
					OY.fois(-tailleScene));
			PMPC = pointA.moins(camera.position).produitScalaire(PC);
			lambda = PCNorme2 / PMPC;
			CM = pointA.moins(camera.cible);

			double aA = lambda * CM.produitScalaire(ii);
			double bA = lambda * CM.produitScalaire(camera.haut);

			int xA = (int) (largeur / 2.0 * (1 + aA / X));
			int yA = (int) (hauteur / 2.0 * (1 - bA / Y));

			Vecteur3 pointB = OX.fois(tailleScene).plus(OZ.fois(i)).plus(
					OY.fois(-tailleScene));
			PMPC = pointB.moins(camera.position).produitScalaire(PC);
			lambda = PCNorme2 / PMPC;
			CM = pointB.moins(camera.cible);

			double aB = lambda * CM.produitScalaire(ii);
			double bB = lambda * CM.produitScalaire(camera.haut);

			int xB = (int) (largeur / 2.0 * (1 + aB / X));
			int yB = (int) (hauteur / 2.0 * (1 - bB / Y));

			g.drawLine(xA, yA, xB, yB);

		}

	}

	static void dessinerScene(Graphics g) {

		Vecteur3 PC = camera.cible.moins(camera.position);
		Vecteur3 ii = new Vecteur3();
		Vecteur3 CM = new Vecteur3();
		ii.x = PC.y * camera.haut.z - PC.z * camera.haut.y;
		ii.y = PC.z * camera.haut.x - PC.x * camera.haut.z;
		ii.z = PC.x * camera.haut.y - PC.y * camera.haut.x;
		ii.normaliser();

		double PCNorme = PC.norme();
		double PCNorme2 = PC.norme2();

		double Y = PCNorme * Math.tan(Math.toRadians(camera.fovy));
		double X = camera.aspect * Y;
		double PMPC;
		double lambda;
		double[] aA = new double[8];
		double[] bA = new double[8];
		int[] xA = new int[8];
		int[] yA = new int[8];

		coinScene[0] = new Vecteur3(-tailleScene, -tailleScene, -tailleScene);
		coinScene[1] = new Vecteur3(-tailleScene, -tailleScene, tailleScene);
		coinScene[2] = new Vecteur3(-tailleScene, tailleScene, -tailleScene);
		coinScene[3] = new Vecteur3(-tailleScene, tailleScene, tailleScene);
		coinScene[4] = new Vecteur3(tailleScene, -tailleScene, -tailleScene);
		coinScene[5] = new Vecteur3(tailleScene, -tailleScene, tailleScene);
		coinScene[6] = new Vecteur3(tailleScene, tailleScene, -tailleScene);
		coinScene[7] = new Vecteur3(tailleScene, tailleScene, tailleScene);

		for (int i = 0; i < 8; i++) {

			PMPC = coinScene[i].moins(camera.position).produitScalaire(PC);
			lambda = PCNorme2 / PMPC;
			CM = coinScene[i].moins(camera.cible);
			aA[i] = lambda * CM.produitScalaire(ii);
			bA[i] = lambda * CM.produitScalaire(camera.haut);
			xA[i] = (int) (largeur / 2.0 * (1 + aA[i] / X));
			yA[i] = (int) (hauteur / 2.0 * (1 - bA[i] / Y));

		}

		g.drawLine(xA[0], yA[0], xA[1], yA[1]);
		g.drawLine(xA[0], yA[0], xA[2], yA[2]);
		g.drawLine(xA[0], yA[0], xA[4], yA[4]);
		g.drawLine(xA[1], yA[1], xA[3], yA[3]);
		g.drawLine(xA[1], yA[1], xA[5], yA[5]);
		g.drawLine(xA[2], yA[2], xA[3], yA[3]);
		g.drawLine(xA[2], yA[2], xA[6], yA[6]);
		g.drawLine(xA[3], yA[3], xA[7], yA[7]);
		g.drawLine(xA[4], yA[4], xA[5], yA[5]);
		g.drawLine(xA[4], yA[4], xA[6], yA[6]);
		g.drawLine(xA[5], yA[5], xA[7], yA[7]);
		g.drawLine(xA[6], yA[6], xA[7], yA[7]);

		dessinerListePairesIndices(g, nSpheresScene, sphereScene);

	}

	static void dessinerSpheresEnCollision(Graphics g) {

		NoeudListePairesIndices noeudCourant = listePairesSpheresEnCollision.head;

		while (noeudCourant != null) {

			dessinerSphere(g, sphereScene[noeudCourant.indiceA]);
			if (noeudCourant.indiceB >= 0)
				dessinerSphere(g, sphereScene[noeudCourant.indiceB]);

			noeudCourant = noeudCourant.suivant;

		}

	}

	static synchronized void avancerSimulation() {

		listePairesSpheresEnCollision.head = null;

		// on essaye d'avancer

		for (int i = 0; i < nSpheresScene; i++) {

			sphereScene[i].centre.x += vitesseSphereScene[i].x;
			sphereScene[i].centre.y += vitesseSphereScene[i].y;
			sphereScene[i].centre.z += vitesseSphereScene[i].z;

		}

		// on detecte les collisions potentielles

		long t0 = System.currentTimeMillis();
		if (modeCollision == 0)
			TD56.detecterCollisionsEntreSpheresQuadratique(sphereScene,
					listePairesSpheresEnCollision);
		else if (modeCollision == 1)
			TD56.detecterCollisionsEntreSpheresOptimise(sphereScene,
					listePairesSpheresEnCollision);
		else if (modeCollision == 2) {

			ListePairesIndices listePairesSpheresEnCollisionTest = new ListePairesIndices();
			listePairesSpheresEnCollisionTest.head = null;

			TD56.detecterCollisionsEntreSpheresQuadratique(sphereScene,
					listePairesSpheresEnCollision);
			TD56.detecterCollisionsEntreSpheresOptimise(sphereScene,
					listePairesSpheresEnCollisionTest);

			if (listePairesSpheresEnCollision.longueur() != listePairesSpheresEnCollisionTest
					.longueur()) {

				System.out.println("Quadratique:");

				NoeudListePairesIndices noeudCourant = listePairesSpheresEnCollision.head;
				while (noeudCourant != null) {

					System.out.println(noeudCourant.indiceA + " "
							+ noeudCourant.indiceB);
					noeudCourant = noeudCourant.suivant;

				}

				System.out.println("Optimise:");
				noeudCourant = listePairesSpheresEnCollisionTest.head;
				while (noeudCourant != null) {

					System.out.println(noeudCourant.indiceA + " "
							+ noeudCourant.indiceB);
					noeudCourant = noeudCourant.suivant;

				}

				System.out.println();

			}

		}
		TD56.detecterCollisionsAvecMurs(sphereScene, pointMurScene,
				normaleMurScene, listePairesSpheresEnCollision);
		long t1 = System.currentTimeMillis();
		tempsDeCalcul = t1 - t0;

		// on remet les spheres a leur place initiale

		for (int i = 0; i < nSpheresScene; i++) {

			sphereScene[i].centre.x -= vitesseSphereScene[i].x;
			sphereScene[i].centre.y -= vitesseSphereScene[i].y;
			sphereScene[i].centre.z -= vitesseSphereScene[i].z;

		}

		// on corrige les vitesses des spheres qui sont entrees en collision

		NoeudListePairesIndices noeudCourant = listePairesSpheresEnCollision.head;

		while (noeudCourant != null) {

			vitesseSphereScene[noeudCourant.indiceA].x *= -1.0;
			vitesseSphereScene[noeudCourant.indiceA].y *= -1.0;
			vitesseSphereScene[noeudCourant.indiceA].z *= -1.0;

			if (noeudCourant.indiceB >= 0) {

				vitesseSphereScene[noeudCourant.indiceB].x *= -1.0;
				vitesseSphereScene[noeudCourant.indiceB].y *= -1.0;
				vitesseSphereScene[noeudCourant.indiceB].z *= -1.0;

			}

			noeudCourant = noeudCourant.suivant;

		}

		// on avance a l'aide des vitesses corrigees

		for (int i = 0; i < nSpheresScene; i++) {

			sphereScene[i].centre.x += vitesseSphereScene[i].x;
			sphereScene[i].centre.y += vitesseSphereScene[i].y;
			sphereScene[i].centre.z += vitesseSphereScene[i].z;

		}

	}

	static synchronized void initialisationScene() {

		// initialisation murs

		pointMurScene = new Vecteur3[6];
		normaleMurScene = new Vecteur3[6];
		pointMurScene[0] = new Vecteur3(-tailleScene, 0, 0);
		normaleMurScene[0] = new Vecteur3(1, 0, 0);
		pointMurScene[1] = new Vecteur3(tailleScene, 0, 0);
		normaleMurScene[1] = new Vecteur3(-1, 0, 0);
		pointMurScene[2] = new Vecteur3(0, -tailleScene, 0);
		normaleMurScene[2] = new Vecteur3(0, 1, 0);
		pointMurScene[3] = new Vecteur3(0, tailleScene, 0);
		normaleMurScene[3] = new Vecteur3(0, -1, 0);
		pointMurScene[4] = new Vecteur3(0, 0, -tailleScene);
		normaleMurScene[4] = new Vecteur3(0, 0, 1);
		pointMurScene[5] = new Vecteur3(0, 0, tailleScene);
		normaleMurScene[5] = new Vecteur3(0, 0, -1);

		coinScene = new Vecteur3[8];
		coinScene[0] = new Vecteur3(-tailleScene, -tailleScene, -tailleScene);
		coinScene[1] = new Vecteur3(-tailleScene, -tailleScene, tailleScene);
		coinScene[2] = new Vecteur3(-tailleScene, tailleScene, -tailleScene);
		coinScene[3] = new Vecteur3(-tailleScene, tailleScene, tailleScene);
		coinScene[4] = new Vecteur3(tailleScene, -tailleScene, -tailleScene);
		coinScene[5] = new Vecteur3(tailleScene, -tailleScene, tailleScene);
		coinScene[6] = new Vecteur3(tailleScene, tailleScene, -tailleScene);
		coinScene[7] = new Vecteur3(tailleScene, tailleScene, tailleScene);

		// initialisation objets

		listePairesSpheresEnCollision = new ListePairesIndices();

		System.out.print("Creation de la scene...");

		sphereScene = new Sphere[nSpheresScene];
		vitesseSphereScene = new Vecteur3[nSpheresScene];
		double rayonSpheres = facteurRayon
				* 0.1
				* Math.pow(tailleScene * tailleScene * tailleScene
						/ nSpheresScene, 1.0 / 3.0);

		for (int i = 0; i < nSpheresScene; i++) {

			sphereScene[i] = new Sphere();
			vitesseSphereScene[i] = (new Vecteur3(rnd.nextDouble() - 0.5, rnd
					.nextDouble() - 0.5, rnd.nextDouble() - 0.5))
					.fois(facteurVitesse);
			boolean creationOK = false;

			while (!creationOK) {

				creationOK = true;

				sphereScene[i].centre.x = 2.0 * tailleScene
						* (rnd.nextDouble() - 0.5);
				sphereScene[i].centre.y = 2.0 * tailleScene
						* (rnd.nextDouble() - 0.5);
				sphereScene[i].centre.z = 2.0 * tailleScene
						* (rnd.nextDouble() - 0.5);
				sphereScene[i].rayon = rayonSpheres * (1.0 + rnd.nextDouble());

				for (int j = 0; j < i; j++)
					if (TD56.intersectionSphereSphere(sphereScene[i],
							sphereScene[j])) {
						creationOK = false;
						break;
					}
				for (int j = 0; j < 6; j++)
					if (TD56.intersectionSphereDemiEspace(sphereScene[i],
							pointMurScene[j], normaleMurScene[j]))
						creationOK = false;

			}

		}

		System.out.println("OK");

		// camera

		camera = new Camera();
		camera.cible = new Vecteur3(0, 0, 0);
		camera.position = new Vecteur3(0, 0, 2.5 * tailleScene);
		camera.haut = new Vecteur3(0, 1, 0);
		camera.fovy = 30;
		camera.aspect = (double) largeur / hauteur;

		TD56.initialiserIntervalles(sphereScene);
		TD56
				.initialiserMatriceCollisionsEtListePairesBoitesEnCollision(sphereScene);

		simulationActive = true;

	}

	static void demarrer() {

		initialisationScene();

		final Label labelTemps = new Label("Temps de calcul : 0 ms");

		final Label labelModeCollision = new Label("Mode collision");

		final CheckboxGroup groupeCollision = new CheckboxGroup();
		final Checkbox groupeCollisionItem1 = new Checkbox("Quadratique",
				groupeCollision, false);
		final Checkbox groupeCollisionItem2 = new Checkbox("Optimise",
				groupeCollision, false);
		final Checkbox groupeCollisionItem3 = new Checkbox("Test",
				groupeCollision, true);

		final Canvas canevas = new Canvas() {

			static final long serialVersionUID = 0;

			public void update(Graphics g) {

				paint(g);

			}

			public void paint(Graphics g) {

				resetBuffer();

				if (graphics2 != null)
					paintBuffer(graphics2);
				if (g != null)
					g.drawImage(offscreen, 0, 0, this);

			}

			public void paintBuffer(Graphics g) {

				labelTemps
						.setText("Temps de calcul : " + tempsDeCalcul + " ms");

				g.setColor(Color.white);
				g.fillRect(0, 0, largeur, hauteur);

				g.setColor(Color.black);
				dessinerSol(g);
				dessinerScene(g);

				g.setColor(Color.red);
				dessinerSpheresEnCollision(g);

			}

			public void resetBuffer() {

				if (graphics2 != null) {

					graphics2.dispose();
					graphics2 = null;

				}

				if (offscreen != null) {

					offscreen.flush();
					offscreen = null;

				}

				System.gc();
				offscreen = bas.createImage(largeur, hauteur);
				graphics2 = offscreen.getGraphics();

			}

		};

		canevas.setBackground(Color.WHITE);
		canevas.setSize(largeur, hauteur);

		final Panel haut = new Panel();

		haut.setLayout(new GridLayout(0, 4));

		haut.add(labelModeCollision);
		haut.add(groupeCollisionItem1);
		haut.add(groupeCollisionItem2);
		haut.add(groupeCollisionItem3);

		haut.add(labelTemps);
		haut.add(new Label());
		haut.add(new Label());
		haut.add(new Label());

		haut.setBackground(Color.gray);

		bas = new Panel();
		bas.setSize(largeur, hauteur);
		bas.add(canevas);

		final ItemListener itemListener = new ItemListener() {
			public synchronized void itemStateChanged(ItemEvent ae) {

				simulationActive = false;

				if (groupeCollisionItem1.getState())
					modeCollision = 0;
				else if (groupeCollisionItem2.getState())
					modeCollision = 1;
				else
					modeCollision = 2;

				if (modeCollision != 0) {

					TD56.initialiserIntervalles(sphereScene);
					TD56.initialiserMatriceCollisionsEtListePairesBoitesEnCollision(sphereScene);

				}

				simulationActive = true;

			}
		};
		groupeCollisionItem1.addItemListener(itemListener);
		groupeCollisionItem2.addItemListener(itemListener);
		groupeCollisionItem3.addItemListener(itemListener);

		// interface souris

		canevas.addMouseListener(new MouseListener() {

			// evenements souris

			public void mouseClicked(MouseEvent e) {
				sourisCliquee = true;
				positionSourisXPrecedent = e.getX();
				positionSourisYPrecedent = e.getY();
			}

			public void mousePressed(MouseEvent e) {

				boutonSouris1 = false;
				boutonSouris2 = false;
				boutonSouris3 = false;
				if (e.getButton() == MouseEvent.BUTTON1)
					boutonSouris1 = true;
				if (e.getButton() == MouseEvent.BUTTON2)
					boutonSouris2 = true;
				if (e.getButton() == MouseEvent.BUTTON3)
					boutonSouris3 = true;

				positionSourisXPrecedent = e.getX();
				positionSourisYPrecedent = e.getY();
			}

			public void mouseReleased(MouseEvent e) {
				sourisCliquee = false;
			}

			public void mouseEntered(MouseEvent e) {

			}

			public void mouseExited(MouseEvent e) {

			}

		});

		canevas.addMouseMotionListener(new MouseMotionListener() {

			// evenements souris

			public void mouseDragged(MouseEvent e) {

				if (modeVisualisation == 8)
					modeVisualisation = 0;
				if (modeVisualisation == 9)
					modeVisualisation = 0;
				if (modeVisualisation == 10)
					modeVisualisation = 0;

				if (boutonSouris1) {

					Vecteur3 CP = camera.position.moins(camera.cible);
					Vecteur3 increment = camera.haut.fois(
							-0.02 * (e.getX() - positionSourisXPrecedent))
							.produitVectoriel(CP);
					camera.position = camera.position.plus(increment);
					camera.position = camera.cible.plus(camera.position.moins(
							camera.cible).fois(
							CP.norme()
									/ camera.position.moins(camera.cible)
											.norme()));

					CP = camera.position.moins(camera.cible);
					Vecteur3 axe = camera.haut.produitVectoriel(CP);
					axe.normaliser();
					increment = axe.fois(
							-0.02 * (e.getY() - positionSourisYPrecedent))
							.produitVectoriel(CP);
					camera.position = camera.position.plus(increment);
					camera.position = camera.cible.plus(camera.position.moins(
							camera.cible).fois(
							CP.norme()
									/ camera.position.moins(camera.cible)
											.norme()));
					CP = camera.position.moins(camera.cible);
					camera.haut = CP.produitVectoriel(axe);
					camera.haut.normaliser();

					positionSourisXPrecedent = e.getX();
					positionSourisYPrecedent = e.getY();
					canevas.repaint();

				}

				if (boutonSouris2) {

					Vecteur3 CP = camera.position.moins(camera.cible);
					double zoom = 1.0 + 0.01 * (e.getY() - positionSourisYPrecedent);
					CP = CP.fois(zoom);

					camera.position = camera.cible.plus(CP);

					positionSourisXPrecedent = e.getX();
					positionSourisYPrecedent = e.getY();
					canevas.repaint();

				}

				if (boutonSouris3) {

					Vecteur3 CP = camera.position.moins(camera.cible);
					Vecteur3 axe = camera.haut.produitVectoriel(CP);
					axe.normaliser();
					camera.position = camera.position.plus(axe.fois(-0.1
							* (e.getX() - positionSourisXPrecedent)));
					camera.position = camera.position.plus(camera.haut
							.fois(0.1 * (e.getY() - positionSourisYPrecedent)));
					camera.cible = camera.cible.plus(axe.fois(-0.1
							* (e.getX() - positionSourisXPrecedent)));
					camera.cible = camera.cible.plus(camera.haut.fois(0.1 * (e
							.getY() - positionSourisYPrecedent)));
					positionSourisXPrecedent = e.getX();
					positionSourisYPrecedent = e.getY();
					canevas.repaint();

				}

			}

			public void mouseMoved(MouseEvent e) {
			}

		});

		final Frame fenetre = new Frame("Simulation");

		fenetre.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});

		fenetre.add(haut, BorderLayout.NORTH);
		fenetre.add(bas, BorderLayout.CENTER);
		fenetre.pack();
		fenetre.setBounds(40, 40, largeur, hauteur + haut.getHeight() + 40);
		fenetre.setBackground(Color.white);
		fenetre.setForeground(Color.black);
		fenetre.setVisible(true);
		offscreen = bas.createImage(largeur, hauteur);
		graphics2 = offscreen.getGraphics();

		while (true) {

			long t0 = System.currentTimeMillis();

			if (t0 - tempsCourant > 30 && simulationActive) {

				avancerSimulation();
				canevas.repaint();

				tempsCourant = t0;

			}

		}

	}

}
