class NoeudListePairesIndices {

	int indiceA;
	int indiceB;

	NoeudListePairesIndices suivant;

	NoeudListePairesIndices() {

		indiceA = -1;
		indiceA = -1;

		suivant = null;

	}

	NoeudListePairesIndices(int indiceA, int indiceB,
			NoeudListePairesIndices suivant) {

		this.indiceA = indiceA;
		this.indiceB = indiceB;

		this.suivant = suivant;

	}

}
