class TestFileTriangles {

	public static void main(String args[]) {

		FileTriangles fileOrdonnee = new FileTriangles();

		fileOrdonnee.insererTriangles(null, null, null, 2.0);
		fileOrdonnee.insererTriangles(null, null, null, 27.3);
		fileOrdonnee.insererTriangles(null, null, null, -1.7);
		fileOrdonnee.insererTriangles(null, null, null, 12.1);
		fileOrdonnee.insererTriangles(null, null, null, -3.0);

		FileTriangles moitieFile = TD56.dichotomie(fileOrdonnee);

		if (TD56.longueurFile(fileOrdonnee) != 2) {

			System.out.println("Erreur, la taille de la file fileOrdonnee doit etre egale a 2.");
			return;

		}

		if (TD56.longueurFile(moitieFile) != 3) {

			System.out.println("Erreur, la taille de la file moitieFile doit etre egale a 3.");
			return;

		}

		// les lignes suivantes doivent afficher :
		// -3.0 / -1.7 / null
		// 2.0 / 12.1 / 27.3 / null

		System.out.println(fileOrdonnee.head.priorite + " / "
				+ fileOrdonnee.head.suivant.priorite + " / "
				+ fileOrdonnee.head.suivant.suivant);
		System.out.println(moitieFile.head.priorite + " / "
				+ moitieFile.head.suivant.priorite + " / "
				+ moitieFile.head.suivant.suivant.priorite + " / "
				+ moitieFile.head.suivant.suivant.suivant);

	}

}
