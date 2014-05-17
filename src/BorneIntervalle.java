class BorneIntervalle {

	double borne;
	boolean debutIntervalle;
	int indice;

	BorneIntervalle() {

		borne = 0.0;
		debutIntervalle = false;
		indice = -1;

	}

	BorneIntervalle(double borne, boolean debutIntervalle, int indice) {

		this.borne = borne;
		this.debutIntervalle = debutIntervalle;
		this.indice = indice;

	}

}
