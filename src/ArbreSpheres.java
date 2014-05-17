class ArbreSpheres {

	Sphere sphere;

	ArbreSpheres filsGauche;
	ArbreSpheres filsDroit;

	Triangle triangle;

	ArbreSpheres() {

		sphere = null;
		filsGauche = null;
		filsDroit = null;
		triangle = null;

	}

	ArbreSpheres(Sphere sphere, ArbreSpheres filsGauche,
			ArbreSpheres filsDroit, Triangle triangle) {

		this.sphere = sphere;
		this.filsGauche = filsGauche;
		this.filsDroit = filsDroit;
		this.triangle = triangle;

	}

}