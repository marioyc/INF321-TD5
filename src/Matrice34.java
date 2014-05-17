class Matrice34 {

	// cette classe implemente une matrice homogene 3x4, qui contient la
	// position (vecteur 3) et l'orientation (matrice 3x3) d'un surface 3D.
	// la position est contenue dans la 4eme colonne

	double[][] m;

	Matrice34() {

		// matrice identite

		m = new double[3][4];

		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 4; j++)
				m[i][j] = 0.0;

		m[0][0] = 1.0;
		m[1][1] = 1.0;
		m[2][2] = 1.0;

	}

	void rotationDepuisAxeEtAngle(Vecteur3 axe, double angle) {

		double fCos = Math.cos(angle);
		double fSin = Math.sin(angle);
		double f1 = 1.0 - fCos;
		double fX2 = axe.x * axe.x;
		double fY2 = axe.y * axe.y;
		double fZ2 = axe.z * axe.z;
		double fXYM = axe.x * axe.y * f1;
		double fXZM = axe.x * axe.z * f1;
		double fYZM = axe.y * axe.z * f1;
		double fXSin = axe.x * fSin;
		double fYSin = axe.y * fSin;
		double fZSin = axe.z * fSin;

		m[0][0] = fX2 * f1 + fCos;
		m[0][1] = fXYM - fZSin;
		m[0][2] = fXZM + fYSin;
		m[1][0] = fXYM + fZSin;
		m[1][1] = fY2 * f1 + fCos;
		m[1][2] = fYZM - fXSin;
		m[2][0] = fXZM - fYSin;
		m[2][1] = fYZM + fXSin;
		m[2][2] = fZ2 * f1 + fCos;

	}

	Matrice34 fois(Matrice34 n) {

		// multiplication de deux matrices homogenes

		Matrice34 res = new Matrice34();

		res.m[0][0] = m[0][0] * n.m[0][0] + m[0][1] * n.m[1][0] + m[0][2]
				* n.m[2][0];
		res.m[1][0] = m[1][0] * n.m[0][0] + m[1][1] * n.m[1][0] + m[1][2]
				* n.m[2][0];
		res.m[2][0] = m[2][0] * n.m[0][0] + m[2][1] * n.m[1][0] + m[2][2]
				* n.m[2][0];
		res.m[0][1] = m[0][0] * n.m[0][1] + m[0][1] * n.m[1][1] + m[0][2]
				* n.m[2][1];
		res.m[1][1] = m[1][0] * n.m[0][1] + m[1][1] * n.m[1][1] + m[1][2]
				* n.m[2][1];
		res.m[2][1] = m[2][0] * n.m[0][1] + m[2][1] * n.m[1][1] + m[2][2]
				* n.m[2][1];
		res.m[0][2] = m[0][0] * n.m[0][2] + m[0][1] * n.m[1][2] + m[0][2]
				* n.m[2][2];
		res.m[1][2] = m[1][0] * n.m[0][2] + m[1][1] * n.m[1][2] + m[1][2]
				* n.m[2][2];
		res.m[2][2] = m[2][0] * n.m[0][2] + m[2][1] * n.m[1][2] + m[2][2]
				* n.m[2][2];
		res.m[0][3] = m[0][0] * n.m[0][3] + m[0][1] * n.m[1][3] + m[0][2]
				* n.m[2][3] + m[0][3];
		res.m[1][3] = m[1][0] * n.m[0][3] + m[1][1] * n.m[1][3] + m[1][2]
				* n.m[2][3] + m[1][3];
		res.m[2][3] = m[2][0] * n.m[0][3] + m[2][1] * n.m[1][3] + m[2][2]
				* n.m[2][3] + m[2][3];

		return res;

	}

	Matrice34 inverse() {

		// cette fonction calcule l'inverse d'une matrice homogene

		Matrice34 inv = new Matrice34();

		inv.m[0][0] = m[0][0];
		inv.m[0][1] = m[1][0];
		inv.m[0][2] = m[2][0];
		inv.m[1][0] = m[0][1];
		inv.m[1][1] = m[1][1];
		inv.m[1][2] = m[2][1];
		inv.m[2][0] = m[0][2];
		inv.m[2][1] = m[1][2];
		inv.m[2][2] = m[2][2];
		inv.m[0][3] = -(m[0][3] * m[0][0] + m[1][3] * m[1][0] + m[2][3]
				* m[2][0]);
		inv.m[1][3] = -(m[0][3] * m[0][1] + m[1][3] * m[1][1] + m[2][3]
				* m[2][1]);
		inv.m[2][3] = -(m[0][3] * m[0][2] + m[1][3] * m[1][2] + m[2][3]
				* m[2][2]);

		return inv;

	}

}