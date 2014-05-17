class ListePairesIndices {

	NoeudListePairesIndices head;

	ListePairesIndices() {

		head = null;

	}

	void inserer(int indiceA, int indiceB) {

		head = new NoeudListePairesIndices(indiceA, indiceB, head);

	}

	int longueur() {

		// cette fonction renvoie la longueur de la liste

		int longueur = 0;
		NoeudListePairesIndices noeudCourant = head;
		while (noeudCourant != null) {
			longueur++;
			noeudCourant = noeudCourant.suivant;
		}

		return longueur;

	}

}
