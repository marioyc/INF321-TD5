class Sphere {

	double rayon;
	Vecteur3 centre;

	Sphere() {

		rayon = 0.0;
		centre = new Vecteur3();

	}

	Sphere(Vecteur3[] s) {

		// cette fonction construit une sphere qui englobe chacun des sommets
		// dans le tableau s

		// calcul du centre de gravite

		centre = new Vecteur3();

		for (int i = 0; i < s.length; i++) {

			centre.x += s[i].x;
			centre.y += s[i].y;
			centre.z += s[i].z;

		}

		centre.x /= s.length;
		centre.y /= s.length;
		centre.z /= s.length;

		// calcul du plus petit rayon englobant

		rayon = 0.0;

		for (int i = 0; i < s.length; i++) {

			double d = centre.distanceAVecteur(s[i]);
			if (d > rayon)
				rayon = d;

		}

		rayon += 1e-6; // pour avoir une petite marge

	}

	Sphere(FileTriangles file) {

		// cette fonction construit une sphere qui englobe chacun des triangles
		// de la surface s dans la file

		// calcul du centre

		centre = new Vecteur3(0.0, 0.0, 0.0);
		int nTriangles = TD56.longueurFile(file);

		if (nTriangles == 0)
			return;

		Vecteur3 coinMin = new Vecteur3(file.head.triangleA.a);
		Vecteur3 coinMax = new Vecteur3(file.head.triangleA.a);

		NoeudFileTriangles noeudCourant = file.head;

		while (noeudCourant != null) {

			coinMax.x = Math.max(coinMax.x, noeudCourant.triangleA.a.x);
			coinMax.y = Math.max(coinMax.y, noeudCourant.triangleA.a.y);
			coinMax.z = Math.max(coinMax.z, noeudCourant.triangleA.a.z);
			coinMax.x = Math.max(coinMax.x, noeudCourant.triangleA.b.x);
			coinMax.y = Math.max(coinMax.y, noeudCourant.triangleA.b.y);
			coinMax.z = Math.max(coinMax.z, noeudCourant.triangleA.b.z);
			coinMax.x = Math.max(coinMax.x, noeudCourant.triangleA.c.x);
			coinMax.y = Math.max(coinMax.y, noeudCourant.triangleA.c.y);
			coinMax.z = Math.max(coinMax.z, noeudCourant.triangleA.c.z);

			coinMin.x = Math.min(coinMin.x, noeudCourant.triangleA.a.x);
			coinMin.y = Math.min(coinMin.y, noeudCourant.triangleA.a.y);
			coinMin.z = Math.min(coinMin.z, noeudCourant.triangleA.a.z);
			coinMin.x = Math.min(coinMin.x, noeudCourant.triangleA.b.x);
			coinMin.y = Math.min(coinMin.y, noeudCourant.triangleA.b.y);
			coinMin.z = Math.min(coinMin.z, noeudCourant.triangleA.b.z);
			coinMin.x = Math.min(coinMin.x, noeudCourant.triangleA.c.x);
			coinMin.y = Math.min(coinMin.y, noeudCourant.triangleA.c.y);
			coinMin.z = Math.min(coinMin.z, noeudCourant.triangleA.c.z);

			noeudCourant = noeudCourant.suivant;

		}

		centre.x = 0.5 * (coinMin.x + coinMax.x);
		centre.y = 0.5 * (coinMin.y + coinMax.y);
		centre.z = 0.5 * (coinMin.z + coinMax.z);

		// calcul du plus petit rayon englobant

		rayon = 0.0;
		double d;

		noeudCourant = file.head;
		while (noeudCourant != null) {

			d = centre.distanceAVecteur(noeudCourant.triangleA.a);
			if (d > rayon)
				rayon = d;
			d = centre.distanceAVecteur(noeudCourant.triangleA.b);
			if (d > rayon)
				rayon = d;
			d = centre.distanceAVecteur(noeudCourant.triangleA.c);
			if (d > rayon)
				rayon = d;

			noeudCourant = noeudCourant.suivant;

		}

		rayon += 1e-6; // pour avoir une petite marge

	}

}
