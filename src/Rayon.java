class Rayon {

	Vecteur3 pointDeDepart;
	Vecteur3 direction;

	Rayon() {

		pointDeDepart = null;
		direction = null;

	}

	Rayon(Vecteur3 pointDeDepart, Vecteur3 direction) {

		this.pointDeDepart = pointDeDepart;
		this.direction = direction;

	}

	boolean intersectionSphere(Sphere s) {

		// cette fonction renvoie true si et seulement si l'intersection entre
		// la boule et le s.rayon (i.e. la demi-droite)
		// defini par { x : x=pointDeDepart+lambda*direction, lambda reel
		// positif } est non vide.

		Vecteur3 CP = new Vecteur3(pointDeDepart.x - s.centre.x,
				pointDeDepart.y - s.centre.y, pointDeDepart.z - s.centre.z);
		double a = direction.norme2();
		double b = 2.0 * (direction.x * CP.x + direction.y * CP.y + direction.z
				* CP.z);
		double c = CP.norme2() - s.rayon * s.rayon;
		double delta = b * b - 4.0 * a * c;

		if (delta < 0)
			return false;

		double lambda = (-b + Math.sqrt(delta)) / (2.0 * a); // calculer la
																// plus grande
																// racine est
																// suffisant
		if (lambda < 0)
			return false;
		return true;

	}

	boolean intersectionPlan(Vecteur3 point, Vecteur3 normale, Vecteur3 i) {

		// cette fonction renvoie true si et seulement si l'intersection entre
		// la rayon et le plan est non vide.
		// Le point d'intersection est renvoye dans i

		if (Math.abs(normale.produitScalaire(direction)) < 1e-20)
			return false; // le rayon est presque parallele au plan du
							// triangle, on ne gere pas ce cas

		Vecteur3 PA = point.moins(pointDeDepart);
		double lambda = PA.produitScalaire(normale)
				/ direction.produitScalaire(normale);

		if (lambda < 0)
			return false;

		i.x = pointDeDepart.x + lambda * direction.x;
		i.y = pointDeDepart.y + lambda * direction.y;
		i.z = pointDeDepart.z + lambda * direction.z;

		return true;

	}

	boolean intersectionTriangle(Vecteur3 a, Vecteur3 b, Vecteur3 c, Vecteur3 i) {

		// cette fonction renvoie true si et seulement si l'intersection entre
		// le rayon et le triangle abc est non vide.
		// NB: si le rayon est dans le plan du triangle, on declare (peut-etre a
		// tort, que l'intersection est vide)
		// Le point d'intersection est renvoye dans i

		Vecteur3 ab = b.moins(a);
		Vecteur3 ac = c.moins(a);
		Vecteur3 bc = c.moins(b);

		Vecteur3 n = ab.produitVectoriel(ac);
		n.normaliser();

		if (Math.abs(n.produitScalaire(direction)) < 1e-20)
			return false; // le rayon est presque parallele au plan du
							// triangle, on ne gere pas ce cas

		Vecteur3 PA = new Vecteur3(a.x - pointDeDepart.x,
				a.y - pointDeDepart.y, a.z - pointDeDepart.z);
		double lambda = PA.produitScalaire(n) / direction.produitScalaire(n);

		if (lambda < 0)
			return false;

		i.x = pointDeDepart.x + lambda * direction.x;
		i.y = pointDeDepart.y + lambda * direction.y;
		i.z = pointDeDepart.z + lambda * direction.z;

		Vecteur3 ai = i.moins(a);
		Vecteur3 bi = i.moins(b);
		Vecteur3 ci = i.moins(c);

		double A = 0.5 * (bi.produitVectoriel(bc)).norme();
		double B = 0.5 * (ci.produitVectoriel(ac)).norme();
		double C = 0.5 * (ai.produitVectoriel(ab)).norme();
		double abc = 0.5 * (ab.produitVectoriel(ac)).norme();
		if (A + B + C > abc + 1e-5)
			return false;
		return true;

	}

	boolean intersectionTriangleSegment(Vecteur3 a, Vecteur3 b, Vecteur3 c) {

		// cette fonction renvoie true si et seulement si l'intersection entre
		// le segment [pointDeDepart,pointDeDepart+direction] et le triangle abc
		// est non vide.
		// NB: si le rayon est dans le plan du triangle, on declare (peut-etre a
		// tort, que l'intersection est vide)
		// Le point d'intersection est renvoye dans i

		Vecteur3 ab = b.moins(a);
		Vecteur3 ac = c.moins(a);
		Vecteur3 bc = c.moins(b);

		Vecteur3 n = ab.produitVectoriel(ac);
		n.normaliser();

		if (Math.abs(n.produitScalaire(direction)) < 1e-20)
			return false; // le rayon est presque parallele au plan du
							// triangle, on ne gere pas ce cas

		Vecteur3 PA = new Vecteur3(a.x - pointDeDepart.x,
				a.y - pointDeDepart.y, a.z - pointDeDepart.z);
		double lambda = PA.produitScalaire(n) / direction.produitScalaire(n);

		if (lambda < 0)
			return false;
		if (lambda > 1)
			return false;

		Vecteur3 i = pointDeDepart.plus(direction.fois(lambda));

		Vecteur3 ai = i.moins(a);
		Vecteur3 bi = i.moins(b);
		Vecteur3 ci = i.moins(c);

		double A = 0.5 * (bi.produitVectoriel(bc)).norme();
		double B = 0.5 * (ci.produitVectoriel(ac)).norme();
		double C = 0.5 * (ai.produitVectoriel(ab)).norme();
		double abc = 0.5 * (ab.produitVectoriel(ac)).norme();
		if (A + B + C > abc + 1e-5)
			return false;
		return true;

	}

}
