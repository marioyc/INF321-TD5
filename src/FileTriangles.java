class FileTriangles {

	NoeudFileTriangles head;

	FileTriangles() {

		head = null;

	}

	FileTriangles(NoeudFileTriangles head) {

		this.head = head;

	}

	void insererTriangles(Triangle triangleA, Triangle triangleB,
			Vecteur3 point, double priorite) {

		NoeudFileTriangles nouveauNoeud = new NoeudFileTriangles(triangleA,
				triangleB, point, priorite);

		NoeudFileTriangles noeudCourant = head;
		NoeudFileTriangles noeudPrecedentCourant = null;

		while (noeudCourant != null) {

			if (priorite < noeudCourant.priorite) { // inserer avant le noeud
													// courant

				nouveauNoeud.suivant = noeudCourant;
				if (noeudPrecedentCourant == null)
					head = nouveauNoeud;
				else
					noeudPrecedentCourant.suivant = nouveauNoeud;
				return;

			}

			noeudPrecedentCourant = noeudCourant;
			noeudCourant = noeudCourant.suivant;

		}

		if (noeudPrecedentCourant == null) {

			head = nouveauNoeud;
			return;

		}

		noeudPrecedentCourant.suivant = nouveauNoeud;

	}

}
