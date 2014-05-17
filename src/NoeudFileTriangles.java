class NoeudFileTriangles {

	double priorite;
	Triangle triangleA;
	Triangle triangleB;
	Vecteur3 point;

	NoeudFileTriangles suivant;

	NoeudFileTriangles() {

		priorite = 0;
		triangleA = null;
		triangleB = null;
		point = null;

		suivant = null;

	}

	NoeudFileTriangles(Triangle triangleA, Triangle triangleB, Vecteur3 point,
			double priorite) {

		this.priorite = priorite;
		this.triangleA = triangleA;
		this.triangleB = triangleB;
		this.point = point;

		suivant = null;

	}

}
