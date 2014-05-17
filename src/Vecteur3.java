class Vecteur3 {

	int index;

	double x;
	double y;
	double z;

	Vecteur3() {
		index = 0;
		x = 0.0;
		y = 0.0;
		z = 0.0;
	}

	Vecteur3(Vecteur3 v) {
		this.index = 0;
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	Vecteur3(double x, double y, double z) {
		this.index = 0;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	double distanceAVecteur(Vecteur3 S) {
		return Math.sqrt((S.x - x) * (S.x - x) + (S.y - y) * (S.y - y)
				+ (S.z - z) * (S.z - z));
	}

	Vecteur3 plus(Vecteur3 v) {
		return new Vecteur3(x + v.x,y + v.y,z + v.z);
	}

	Vecteur3 moins(Vecteur3 v) {
		return new Vecteur3(x - v.x,y - v.y,z - v.z);
	}

	Vecteur3 fois(double d) {
		return new Vecteur3(x * d,y * d,z * d);
	}

	Vecteur3 produitVectoriel(Vecteur3 v) {
		return new Vecteur3(y * v.z - z * v.y,-x * v.z + z * v.x,x * v.y - y * v.x);
	}

	double produitScalaire(Vecteur3 v) {
		return x * v.x + y * v.y + z * v.z;
	}

	double norme() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	double norme2() {
		return x * x + y * y + z * z;
	}

	void normaliser() {
		double aux = norme();
		
		if(aux != 0){
			x /= aux;
			y /= aux;
			z /= aux;
		}
	}

	void ajouter(Vecteur3 v) {
		x += v.x;
		y += v.y;
		z += v.z;
	}

	public String toString() {
		return "[ " + x + " ] [ " + y + " ] [ " + z + " ]";
	}

}
