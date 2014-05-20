class TD56 {

	// **********
	// question 1
	// **********

	static synchronized int longueurFile(FileTriangles file) {
		int ret = 0;
		NoeudFileTriangles cur = file.head;
		
		while(cur != null){
			cur = cur.suivant;
			++ret;
		}

		return ret;

	}

	// **********
	// question 2
	// **********

	static synchronized FileTriangles dichotomie(FileTriangles file) {
		int len = longueurFile(file);
		
		NoeudFileTriangles cur = file.head,prev = null;
		
		for(int i = 0;i < len / 2;++i){
			prev = cur;
			cur = cur.suivant;
		}
		
		prev.suivant = null;
		
		return new FileTriangles(cur);
	}

	// **********
	// question 3
	// **********

	static synchronized ArbreSpheres construireArbreSpheresPourTrianglesDansFile(
			FileTriangles file) {

		int len = longueurFile(file);
		
		if(len == 1)
			return new ArbreSpheres(new Sphere(file),null,null,file.head.triangleA);
		
		ArbreSpheres ret = new ArbreSpheres(new Sphere(file),null,null,null);
		
		file = ordonnerTriangles(file);
		FileTriangles file_gauche = file;
		FileTriangles file_droite = dichotomie(file);
		
		ret.filsGauche = construireArbreSpheresPourTrianglesDansFile(file_gauche);
		ret.filsDroit = construireArbreSpheresPourTrianglesDansFile(file_droite);
		
		return ret;
	}

	// **********
	// question 4
	// **********

	static synchronized FileTriangles ordonnerTriangles(FileTriangles file) {
		FileTriangles ret = new FileTriangles();
		NoeudFileTriangles cur = file.head;
		Vecteur3 dir = new Vecteur3(Math.random(),Math.random(),Math.random());
		
		while(cur != null){
			Vecteur3 centre = cur.triangleA.a.plus(cur.triangleA.b).plus(cur.triangleA.c).fois(1.0 / 3);
			double coord = centre.produitScalaire(dir);
			ret.insererTriangles(cur.triangleA, null, null, coord);
			cur = cur.suivant;
		}

		return ret;

	}

	// **********
	// question 5
	// **********

	static synchronized boolean intersectionSphereDemiEspace(Sphere s,
			Vecteur3 point, Vecteur3 normale) {
		normale.normaliser();
		
		Vecteur3 aux = s.centre.moins(point);
		aux = normale.fois(normale.produitScalaire(aux));
		double distance = aux.norme();
		
		return distance <= s.rayon;
	}

	// **********
	// question 6
	// **********

	static synchronized boolean intersectionSurfaceMurVraiFaux(
			ArbreSpheres arbre, Vecteur3 point, Vecteur3 normale) {
		if(intersectionSphereDemiEspace(arbre.sphere, point, normale)){
			if(arbre.filsGauche == null){
				return (arbre.triangle.a.moins(point).produitScalaire(normale) <= 0 ||
						arbre.triangle.b.moins(point).produitScalaire(normale) <= 0 ||
						arbre.triangle.c.moins(point).produitScalaire(normale) <= 0 );
			}else{
				boolean check_gauche = intersectionSurfaceMurVraiFaux(arbre.filsGauche, point, normale);
				boolean check_droit = intersectionSurfaceMurVraiFaux(arbre.filsDroit, point, normale);
				
				return check_gauche || check_droit;
			}
		}else return false;
	}

	// **********
	// question 7
	// **********

	static synchronized void intersectionSurfaceMur(ArbreSpheres arbre,
			Vecteur3 point, Vecteur3 normale, FileTriangles file) {
		if(arbre.filsGauche == null){
			if(arbre.triangle.a.moins(point).produitScalaire(normale) <= 0 ||
					arbre.triangle.b.moins(point).produitScalaire(normale) <= 0 ||
					arbre.triangle.c.moins(point).produitScalaire(normale) <= 0 ){
				file.insererTriangles(arbre.triangle, null, null, 0.0);
			}
		}else{
			intersectionSurfaceMur(arbre.filsGauche, point, normale, file);
			intersectionSurfaceMur(arbre.filsDroit, point, normale, file);
		}
	}

	// **********
	// question 8
	// **********

	static synchronized void intersectionSurfacePolyedre(ArbreSpheres arbre,
			int nFaces, Vecteur3[] point, Vecteur3[] normale, FileTriangles file) {
		if(arbre.filsGauche == null){
			boolean check = true;
			
			for(int i = 0;i < nFaces && check;++i){
				if(arbre.triangle.a.moins(point[i]).produitScalaire(normale[i]) > 0) check = false;
				if(arbre.triangle.b.moins(point[i]).produitScalaire(normale[i]) > 0) check = false;
				if(arbre.triangle.c.moins(point[i]).produitScalaire(normale[i]) > 0) check = false;
			}
			
			if(check)
				file.insererTriangles(arbre.triangle, null, null, 0.0);
		}else{
			intersectionSurfacePolyedre(arbre.filsGauche, nFaces, point, normale, file);
			intersectionSurfacePolyedre(arbre.filsDroit, nFaces, point, normale, file);
		}
	}

	// **********
	// question 9
	// **********

	static synchronized void minimumSurface(ArbreSpheres arbre,
			Vecteur3 minimumCourant, Vecteur3 normale) {
		Vecteur3 aux = arbre.sphere.centre.moins(normale.fois(arbre.sphere.rayon));
		
		if(aux.moins(minimumCourant).produitScalaire(normale) >= 0)
			return;
		
		if(arbre.filsGauche == null){
			if(arbre.triangle.a.moins(minimumCourant).produitScalaire(normale) < 0){
				minimumCourant.x = arbre.triangle.a.x;
				minimumCourant.y = arbre.triangle.a.y;
				minimumCourant.z = arbre.triangle.a.z;
			}

			if(arbre.triangle.b.moins(minimumCourant).produitScalaire(normale) < 0){
				minimumCourant.x = arbre.triangle.b.x;
				minimumCourant.y = arbre.triangle.b.y;
				minimumCourant.z = arbre.triangle.b.z;
			}

			if(arbre.triangle.c.moins(minimumCourant).produitScalaire(normale) < 0){
				minimumCourant.x = arbre.triangle.c.x;
				minimumCourant.y = arbre.triangle.c.y;
				minimumCourant.z = arbre.triangle.c.z;
			}
		}else{
			minimumSurface(arbre.filsGauche, minimumCourant, normale);
			minimumSurface(arbre.filsDroit, minimumCourant, normale);
		}
	}

	// ***********
	// question 10
	// ***********

	static synchronized boolean intersectionSurfaceRayonVraiFaux(
			ArbreSpheres arbre, Rayon rayon) {
		if(!rayon.intersectionSphere(arbre.sphere))
			return false;
		
		if(arbre.filsGauche == null)
			return rayon.intersectionTriangle(arbre.triangle.a, arbre.triangle.b, arbre.triangle.c, new Vecteur3());
		
		return intersectionSurfaceRayonVraiFaux(arbre.filsGauche, rayon) ||
				intersectionSurfaceRayonVraiFaux(arbre.filsDroit, rayon);
	}

	// ***********
	// question 11
	// ***********

	static synchronized void intersectionSurfaceRayon(ArbreSpheres arbre,
			Rayon rayon, FileTriangles file) {
		if(!rayon.intersectionSphere(arbre.sphere))
			return;
		
		if(arbre.filsGauche == null){
			Vecteur3 i = new Vecteur3();
			
			if(rayon.intersectionTriangle(arbre.triangle.a, arbre.triangle.b, arbre.triangle.c, i))
				file.insererTriangles(arbre.triangle,null,i,rayon.pointDeDepart.moins(i).norme());
		}else{
			intersectionSurfaceRayon(arbre.filsGauche, rayon, file);
			intersectionSurfaceRayon(arbre.filsDroit, rayon, file);
		}
	}

	// ***********
	// question 12
	// ***********

	static synchronized boolean intersectionSurfaceRayonVraiFauxLineaire(
			Surface surface, Rayon rayon) {
		int n = surface.triangle.length;
		
		for(int i = 0;i < n;++i)
			if(rayon.intersectionTriangle(surface.triangle[i].a, surface.triangle[i].b, surface.triangle[i].c, new Vecteur3()))
				return true;
		
		return false;
	}

	static synchronized void intersectionSurfaceRayonLineaire(Surface surface,
			Rayon rayon, FileTriangles file) {
		int n = surface.triangle.length;
		
		for(int i = 0;i < n;++i){
			Vecteur3 p = new Vecteur3();
			
			if(rayon.intersectionTriangle(surface.triangle[i].a, surface.triangle[i].b, surface.triangle[i].c, p))
				file.insererTriangles(surface.triangle[i], null, p, rayon.pointDeDepart.moins(p).norme());
		}
	}

	// ***********
	// question 13
	// ***********

	static synchronized boolean intersectionSphereSphere(Sphere sA, Sphere sB) {
		return sA.centre.moins(sB.centre).norme() <= sA.rayon + sB.rayon;

	}

	// ***********
	// question 14
	// ***********

	static synchronized void intersectionSurfaceSurface(ArbreSpheres arbreA,
			ArbreSpheres arbreB, FileTriangles file) {
		if(!intersectionSphereSphere(arbreA.sphere,arbreB.sphere))
			return;
		
		if(arbreA.filsGauche == null && arbreB.filsGauche == null){
			if(Triangle.intersectionTriangleTriangle(arbreA.triangle.a,arbreA.triangle.b,arbreA.triangle.c,arbreB.triangle.a,arbreB.triangle.b,arbreB.triangle.c))
				file.insererTriangles(arbreA.triangle, arbreB.triangle, null, 0.0);
		}else if(arbreA.filsGauche == null){
			intersectionSurfaceSurface(arbreA,arbreB.filsGauche,file);
			intersectionSurfaceSurface(arbreA,arbreB.filsDroit,file);
		}else if(arbreB.filsGauche == null){
			intersectionSurfaceSurface(arbreA.filsGauche,arbreB,file);
			intersectionSurfaceSurface(arbreA.filsDroit,arbreB,file);
		}else{
			intersectionSurfaceSurface(arbreA.filsGauche,arbreB.filsGauche,file);
			intersectionSurfaceSurface(arbreA.filsGauche,arbreB.filsDroit,file);
			intersectionSurfaceSurface(arbreA.filsDroit,arbreB.filsGauche,file);
			intersectionSurfaceSurface(arbreA.filsDroit,arbreB.filsDroit,file);
		}
	}

	// ***********
	// question 15
	// ***********

	static synchronized void detecterCollisionsAvecMurs(Sphere[] sphere,
			Vecteur3[] point, Vecteur3[] normale,
			ListePairesIndices listePairesIndices) {

		// contenu a modifier

	}

	// ***********
	// question 16
	// ***********

	static synchronized void detecterCollisionsEntreSpheresQuadratique(
			Sphere[] sphere, ListePairesIndices listePairesIndices) {

		// contenu a modifier

	}

	// ***********
	// question 17
	// ***********

	static synchronized void echangerBornes(BorneIntervalle a, BorneIntervalle b) {

		// contenu a modifier

	}

	// ***********
	// question 18
	// ***********

	static synchronized void trierBornesParInsertion(
			BorneIntervalle[] borneIntervalle) {

		// contenu a modifier

	}

	// ***********
	// question 19
	// ***********

	static void initialiserIntervalles(Sphere[] sphere) {

		// contenu a modifier

	}

	// ***********
	// question 20
	// ***********

	static void initialiserMatriceCollisionsEtListePairesBoitesEnCollision(
			Sphere[] sphere) {

		// contenu a modifier

	}

	// ***********
	// question 21
	// ***********

	static synchronized void miseAJourIntervalles(Sphere[] sphere) {

		// contenu a modifier

	}

	// ***********
	// question 22
	// ***********

	static synchronized void triBornesEtMiseAjourMatriceSimultanes(
			BorneIntervalle[] borneIntervalle, int axe) {

		// contenu a modifier

	}

	// ***********
	// question 23
	// ***********

	static synchronized boolean supprimer(ListePairesIndices liste,
			int indiceA, int indiceB) {

		// contenu a modifier

		return false;

	}

	// ***********
	// question 24
	// ***********

	static synchronized void miseAjourListePairesBoitesEnCollision(
			BorneIntervalle[] borneIntervalle, int axe) {

		// contenu a modifier

	}

	// ***********
	// question 25
	// ***********

	static synchronized void detecterCollisionsEntreSpheresOptimise(
			Sphere[] sphere, ListePairesIndices listePairesIndices) {

		// contenu a modifier

	}

	// *************
	// fonction main
	// *************

	public static synchronized void main(String[] args) {
		
		// pour les questions 1 a 14

		InterfaceA.demarrer();

		// pour les questions 15 a 29

		// InterfaceB.demarrer();

	}

}
