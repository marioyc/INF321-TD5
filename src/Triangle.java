class Triangle {

	Vecteur3 a;
	Vecteur3 b;
	Vecteur3 c;

	Triangle() {
		a = null;
		b = null;
		c = null;
	}

	Triangle(Vecteur3 a, Vecteur3 b, Vecteur3 c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	static boolean intersectionTriangleTriangle(Vecteur3 a, Vecteur3 b,
			Vecteur3 c, Vecteur3 d, Vecteur3 e, Vecteur3 f) {

		// pour vous faciliter la tache, cette fonction est deja programmee

		Rayon ab = new Rayon(a, b.moins(a));
		Rayon ac = new Rayon(a, c.moins(a));
		Rayon bc = new Rayon(b, c.moins(b));

		if (ab.intersectionTriangleSegment(d, e, f))
			return true;
		if (ac.intersectionTriangleSegment(d, e, f))
			return true;
		if (bc.intersectionTriangleSegment(d, e, f))
			return true;

		Rayon de = new Rayon(d, e.moins(d));
		Rayon df = new Rayon(d, f.moins(d));
		Rayon ef = new Rayon(e, f.moins(e));

		if (de.intersectionTriangleSegment(a, b, c))
			return true;
		if (df.intersectionTriangleSegment(a, b, c))
			return true;
		if (ef.intersectionTriangleSegment(a, b, c))
			return true;

		return false;

	}

}
