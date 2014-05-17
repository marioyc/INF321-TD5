import java.awt.*;
import java.awt.event.*;
import java.io.*;

import java.util.Random;

class InterfaceA {

	// ***********************
	// description de la scene
	// ***********************
	
	static Color			couleurCiel=new Color(100,150,255);		// couleur du ciel 
	static Color			couleurSol=new Color(30,150,60);		// couleur du sol
	
	static Color			couleurSurface0=new Color(255,255,50);	// couleur du premier objet
	static Color			couleurSurface1=new Color(255,50,50);	// couleur du deuxieme objet
	
	static Surface[]		surfaceScene;							// liste des objets de la scene
	static Matrice34[]		transformationSurface;					// positions des objets
	static Sphere[]			sphereEnglobante;						// sphere englobantee de chaque objet
	static Color[]			couleurSurface;							// couleurs des objets de la scene
	

	// *******************
	// interface graphique
	// *******************
		
	static int				largeur=900;							// largeur de la fenetre
	static int				hauteur=600;							// hauteur de la fenetre
	
	static Camera			camera;									// le modele de camera
	
	static boolean			sourisCliquee=false;
	static boolean			boutonSouris1=false;
	static boolean			boutonSouris2=false;
	static boolean			boutonSouris3=false;
	static int				positionSourisXPrecedent=0;
	static int				positionSourisYPrecedent=0;

	static boolean			afficherArbre=false;
	static int				niveauSpheres=0;
	static int				pasSpheres=10;

    static Graphics			graphics;
    static Graphics			graphics2;
    static int				modeVisualisation=0;
    static Image			offscreen;
	static Panel			bas;
	static Random			rnd=new Random(0);
	static Vecteur3			pointSol;
	static Vecteur3			normaleSol;
	static int				pasRayTracing=5;

	static FileTriangles	fileTriangles=null;
	static Rayon			rayonCamera;
	static Vecteur3			positionLampe;
	
	static Vecteur3[][]		sommetAffichage;						// coordonnees des sommets des objets a l'ecran
	static Triangle[][]		triangleAffichage;						// triangles a l'ecran

	
	// ***************
	// tests du TD56
	// ***************
	
	static Vecteur3			pointDemiEspace;
	static Vecteur3			normaleDemiEspace;
	static boolean			intersectionSurfaceMur=false;
	static boolean			intersectionSurfaceRayonCamera=false;
	static int				nFacesPolyedre;
	static Vecteur3			pointPolyedre[];
	static Vecteur3			normalePolyedre[];
	
	static Graphics ouvreFenetre(int l, int h) {

		Frame fenetre = new Frame("Question 1");
		fenetre.addWindowListener(new WindowAdapter() {
  			public void windowClosing(WindowEvent we) {
    				System.out.println("Fenetre fermee, c'est fini.");
    				System.exit(0);
  			}
		});
		fenetre.setBounds(40,40,l,h);
		fenetre.setBackground(Color.white);
		fenetre.setForeground(Color.black);
		fenetre.setVisible(true);
		Graphics dessin = fenetre.getGraphics();
		return dessin;

	}

	static void dessinerArbreSpheres(Graphics g, ArbreSpheres a, int niveau) {
	
		if (a==null) return;
		if (niveau==niveauSpheres) dessinerSphere(g,a.sphere);
		else {

			dessinerArbreSpheres(g,a.filsGauche,niveau+1);
			dessinerArbreSpheres(g,a.filsDroit,niveau+1);

		}
	
	}
	
	static void dessinerSphere(Graphics g, Sphere s) {

		Vecteur3 PC=new Vecteur3(camera.cible.x-camera.position.x,camera.cible.y-camera.position.y,camera.cible.z-camera.position.z);
		Vecteur3 ii=new Vecteur3();
		ii.x=PC.y*camera.haut.z-PC.z*camera.haut.y;
		ii.y=PC.z*camera.haut.x-PC.x*camera.haut.z;
		ii.z=PC.x*camera.haut.y-PC.y*camera.haut.x;
		ii.normaliser();
		
		double PCNorme=PC.norme();
		double PCNorme2=PC.norme2();

		double Y=PCNorme*Math.tan(Math.toRadians(camera.fovy));
		double X=camera.aspect*Y;

		for (int i=0;i<=180;i+=pasSpheres) {
		
			double xPrecedent;
			double yPrecedent;
			double xNouveau=0.0;
			double yNouveau=0.0;
			
			for (int j=0;j<360+pasSpheres;j+=pasSpheres) {
				
				Vecteur3 M=new Vecteur3(s.centre.x+s.rayon*Math.sin(Math.toRadians(i))*Math.cos(Math.toRadians(j)),s.centre.y+s.rayon*Math.cos(Math.toRadians(i)),s.centre.z+s.rayon*Math.sin(Math.toRadians(i))*Math.sin(Math.toRadians(j)));
				Vecteur3 PM=M.moins(camera.position);
				
				double PMPC=PM.produitScalaire(PC);
				double lambda=PCNorme2/PMPC;
				Vecteur3 CM=M.moins(camera.position);
				
				double a=lambda*(CM.x*ii.x+CM.y*ii.y+CM.z*ii.z);	
				double b=lambda*(CM.x*camera.haut.x+CM.y*camera.haut.y+CM.z*camera.haut.z);	
			
				xPrecedent=xNouveau;
				yPrecedent=yNouveau;
				
				xNouveau=largeur/2.0*(1+a/X);
				yNouveau=hauteur/2.0*(1-b/Y);
				
				if (j==0) { 
				
					xPrecedent=xNouveau;
					yPrecedent=yNouveau;
				
				}
			
				g.drawLine((int)xPrecedent,(int)yPrecedent,(int)xNouveau,(int)yNouveau);
				
			}
			
		}
	
		for (int j=0;j<=360+pasSpheres;j+=pasSpheres) {
		
			double xPrecedent;
			double yPrecedent;
			double xNouveau=0.0;
			double yNouveau=0.0;
			
			for (int i=0;i<=180;i+=pasSpheres) {
				
				Vecteur3 M=new Vecteur3(s.centre.x+s.rayon*Math.sin(Math.toRadians(i))*Math.cos(Math.toRadians(j)),s.centre.y+s.rayon*Math.cos(Math.toRadians(i)),s.centre.z+s.rayon*Math.sin(Math.toRadians(i))*Math.sin(Math.toRadians(j)));
				Vecteur3 PM=M.moins(camera.position);
				
				double PMPC=PM.produitScalaire(PC);
				double lambda=PCNorme2/PMPC;
				Vecteur3 CM=M.moins(camera.position);
				
				double a=lambda*(CM.x*ii.x+CM.y*ii.y+CM.z*ii.z);	
				double b=lambda*(CM.x*camera.haut.x+CM.y*camera.haut.y+CM.z*camera.haut.z);	
			
				xPrecedent=xNouveau;
				yPrecedent=yNouveau;
				
				xNouveau=largeur/2.0*(1+a/X);
				yNouveau=hauteur/2.0*(1-b/Y);
				
				if (i==0) { 
				
					xPrecedent=xNouveau;
					yPrecedent=yNouveau;
				
				}
			
				g.drawLine((int)xPrecedent,(int)yPrecedent,(int)xNouveau,(int)yNouveau);
				
			}
			
		}
	
	}
	
	static void rayTraceSurfaceMonochrome(Graphics g, int surfaceIndex) {
	
		Vecteur3 PC=camera.cible.moins(camera.position);
		Vecteur3 ii=new Vecteur3();
		ii.x=PC.y*camera.haut.z-PC.z*camera.haut.y;
		ii.y=PC.z*camera.haut.x-PC.x*camera.haut.z;
		ii.z=PC.x*camera.haut.y-PC.y*camera.haut.x;
		ii.normaliser();
		
		double PCNorme=PC.norme();

		double Y=PCNorme*Math.tan(Math.toRadians(camera.fovy));
		double X=camera.aspect*Y;
	
		rayonCamera.pointDeDepart=camera.position;
		
		for (int y=0;y<hauteur;y+=pasRayTracing) {
				
			for (int x=0;x<largeur;x+=pasRayTracing) {		
		
				double a=(x*2.0/(double)largeur-1)*X;
				double b=(1-y*2.0/(double)hauteur)*Y;
	
				Vecteur3 pointEcran=camera.cible.plus(ii.fois(a)).plus(camera.haut.fois(b));
				rayonCamera.direction=pointEcran.moins(rayonCamera.pointDeDepart);
				rayonCamera.direction.normaliser();
				
				if (TD56.intersectionSurfaceRayonVraiFaux(surfaceScene[surfaceIndex].arbreSpheres,rayonCamera)) g.setColor(couleurSurface[surfaceIndex]); 
				else g.setColor(couleurCiel);

				g.fillRect(x,y,pasRayTracing,pasRayTracing);

			}
			
		}
		
	}

	static void rayTraceSurface(Graphics g, int surfaceIndex) {
	
		Vecteur3 PC=camera.cible.moins(camera.position);
		Vecteur3 ii=new Vecteur3();
		ii.x=PC.y*camera.haut.z-PC.z*camera.haut.y;
		ii.y=PC.z*camera.haut.x-PC.x*camera.haut.z;
		ii.z=PC.x*camera.haut.y-PC.y*camera.haut.x;
		ii.normaliser();
		
		double PCNorme=PC.norme();

		double Y=PCNorme*Math.tan(Math.toRadians(camera.fovy));
		double X=camera.aspect*Y;
		double intensite=0;
	
		rayonCamera.pointDeDepart=camera.position;
		
		for (int y=0;y<hauteur;y+=pasRayTracing) {
				
			for (int x=0;x<largeur;x+=pasRayTracing) {		
		
				double a=(x*2.0/(double)largeur-1)*X;
				double b=(1-y*2.0/(double)hauteur)*Y;
	
				Vecteur3 pointEcran=camera.cible.plus(ii.fois(a)).plus(camera.haut.fois(b));
				rayonCamera.direction=pointEcran.moins(rayonCamera.pointDeDepart);
				rayonCamera.direction.normaliser();
				
				Vecteur3 pointIntersection=new Vecteur3();
				boolean intersectionSol=rayonCamera.intersectionPlan(pointSol,normaleSol,pointIntersection);
				
				fileTriangles.head=null;
				TD56.intersectionSurfaceRayon(surfaceScene[surfaceIndex].arbreSpheres,rayonCamera,fileTriangles);
				
				if (!rayonCamera.intersectionPlan(pointSol,normaleSol,pointIntersection)) g.setColor(couleurCiel);

				if (fileTriangles.head==null) { // intersection avec le sol
				
					if (!intersectionSol) g.setColor(couleurCiel);
					else {
						
						Vecteur3 directionLampe=positionLampe.moins(pointIntersection);
						directionLampe.normaliser();
						Vecteur3 pointDepartOmbre=pointIntersection.plus(directionLampe.fois(0.0001));
											
						intensite=Math.min(255,sphereEnglobante[surfaceIndex].rayon*550*Math.max(0,normaleSol.produitScalaire(directionLampe))/pointIntersection.moins(positionLampe).norme());
						fileTriangles.head=null;
						if (TD56.intersectionSurfaceRayonVraiFaux(surfaceScene[surfaceIndex].arbreSpheres,new Rayon(pointDepartOmbre,directionLampe))) intensite/=3.0;
						if (rayonCamera.pointDeDepart.moins(pointSol).produitScalaire(normaleSol)<0) intensite=20;
						intensite=Math.max(25,intensite);
						g.setColor(new Color((int)(intensite/255.0*couleurSol.getRed()),(int)(intensite/255.0*couleurSol.getGreen()),(int)(intensite/255.0*couleurSol.getBlue())));
						
					}
					
				}
				else if ((!intersectionSol)||((intersectionSol)&&(pointIntersection.moins(camera.position).norme()>fileTriangles.head.point.moins(camera.position).norme()))) {
				
					NoeudFileTriangles n=fileTriangles.head;
					
					Vecteur3 normale=n.triangleA.b.moins(n.triangleA.a).produitVectoriel(n.triangleA.c.moins(n.triangleA.a));
					normale.normaliser();
					Vecteur3 directionLampe=positionLampe.moins(n.point);
					directionLampe.normaliser();
					Vecteur3 pointDepartOmbre=n.point.plus(directionLampe.fois(0.0001));
										
					intensite=Math.min(255,sphereEnglobante[surfaceIndex].rayon*550*Math.max(0,normale.produitScalaire(directionLampe))/n.point.moins(positionLampe).norme());
					fileTriangles.head=null;
					double speculaire=0;
					if (TD56.intersectionSurfaceRayonVraiFaux(surfaceScene[surfaceIndex].arbreSpheres,new Rayon(pointDepartOmbre,directionLampe))) intensite/=3.0;
					else {
						
						Vecteur3 rayonIncident=rayonCamera.direction.fois(-1.0);
						Vecteur3 composanteNormale=normale.fois(rayonIncident.produitScalaire(normale));
						Vecteur3 rayonReflechi=composanteNormale.plus(composanteNormale.moins(rayonIncident).fois(2.0));
						rayonReflechi.normaliser();
						double facteur=(Math.abs(directionLampe.produitScalaire(rayonReflechi)))*Math.max(0,normale.produitScalaire(directionLampe));
						speculaire=255*facteur*facteur*facteur*facteur;
					
					}
					if (rayonCamera.pointDeDepart.moins(pointSol).produitScalaire(normaleSol)<0) { intensite=20;speculaire=0; }
					intensite=Math.max(15,intensite);
					double R=Math.min(255,(intensite/255.0*couleurSurface[surfaceIndex].getRed())+speculaire);
					double G=Math.min(255,(intensite/255.0*couleurSurface[surfaceIndex].getGreen())+speculaire);
					double B=Math.min(255,(intensite/255.0*couleurSurface[surfaceIndex].getBlue())+speculaire);
					g.setColor(new Color((int)R,(int)G,(int)B));
					
				}
				else {
					
					Vecteur3 directionLampe=positionLampe.moins(pointIntersection);
					directionLampe.normaliser();
					Vecteur3 pointDepartOmbre=pointIntersection.plus(directionLampe.fois(0.0001));
										
					intensite=Math.min(255,sphereEnglobante[surfaceIndex].rayon*550*Math.max(0,normaleSol.produitScalaire(directionLampe))/pointIntersection.moins(positionLampe).norme());
					fileTriangles.head=null;
					if (TD56.intersectionSurfaceRayonVraiFaux(surfaceScene[surfaceIndex].arbreSpheres,new Rayon(pointDepartOmbre,directionLampe))) intensite/=3.0;
					if (rayonCamera.pointDeDepart.moins(pointSol).produitScalaire(normaleSol)<0) intensite=20;
					intensite=Math.max(25,intensite);
					g.setColor(new Color((int)(intensite/255.0*couleurSol.getRed()),(int)(intensite/255.0*couleurSol.getGreen()),(int)(intensite/255.0*couleurSol.getBlue())));
					
				}

				g.fillRect(x,y,pasRayTracing,pasRayTracing);

			}
			
		}
		
	}
	
	static void rayTraceSurfaceLineaire(Graphics g, int surfaceIndex) {
	
		Vecteur3 PC=camera.cible.moins(camera.position);
		Vecteur3 ii=new Vecteur3();
		ii.x=PC.y*camera.haut.z-PC.z*camera.haut.y;
		ii.y=PC.z*camera.haut.x-PC.x*camera.haut.z;
		ii.z=PC.x*camera.haut.y-PC.y*camera.haut.x;
		ii.normaliser();
		
		double PCNorme=PC.norme();

		double Y=PCNorme*Math.tan(Math.toRadians(camera.fovy));
		double X=camera.aspect*Y;
		double intensite=0;
	
		rayonCamera.pointDeDepart=camera.position;
		
		for (int y=0;y<hauteur;y+=pasRayTracing) {
				
			for (int x=0;x<largeur;x+=pasRayTracing) {		
		
				double a=(x*2.0/(double)largeur-1)*X;
				double b=(1-y*2.0/(double)hauteur)*Y;
	
				Vecteur3 pointEcran=camera.cible.plus(ii.fois(a)).plus(camera.haut.fois(b));
				rayonCamera.direction=pointEcran.moins(rayonCamera.pointDeDepart);
				rayonCamera.direction.normaliser();
	
				Vecteur3 pointIntersection=new Vecteur3();
				boolean intersectionSol=rayonCamera.intersectionPlan(pointSol,normaleSol,pointIntersection);
				
				fileTriangles.head=null;
				TD56.intersectionSurfaceRayonLineaire(surfaceScene[surfaceIndex],rayonCamera,fileTriangles);
				
				if (!rayonCamera.intersectionPlan(pointSol,normaleSol,pointIntersection)) g.setColor(couleurCiel);

				if (fileTriangles.head==null) { // intersection avec le sol
				
					if (!intersectionSol) g.setColor(couleurCiel);
					else {
						
						Vecteur3 directionLampe=positionLampe.moins(pointIntersection);
						directionLampe.normaliser();
						Vecteur3 pointDepartOmbre=pointIntersection.plus(directionLampe.fois(0.0001));
											
						intensite=Math.min(255,sphereEnglobante[surfaceIndex].rayon*550*Math.max(0,normaleSol.produitScalaire(directionLampe))/pointIntersection.moins(positionLampe).norme());
						fileTriangles.head=null;
						if (TD56.intersectionSurfaceRayonVraiFauxLineaire(surfaceScene[surfaceIndex],new Rayon(pointDepartOmbre,directionLampe))) intensite/=3.0;
						if (rayonCamera.pointDeDepart.moins(pointSol).produitScalaire(normaleSol)<0) intensite=20;
						intensite=Math.max(25,intensite);
						g.setColor(new Color((int)(intensite/255.0*couleurSol.getRed()),(int)(intensite/255.0*couleurSol.getGreen()),(int)(intensite/255.0*couleurSol.getBlue())));
						
					}
					
				}
				else if ((!intersectionSol)||((intersectionSol)&&(pointIntersection.moins(camera.position).norme()>fileTriangles.head.point.moins(camera.position).norme()))) {
				
					NoeudFileTriangles n=fileTriangles.head;
					
					Vecteur3 normale=n.triangleA.b.moins(n.triangleA.a).produitVectoriel(n.triangleA.c.moins(n.triangleA.a));
					normale.normaliser();
					Vecteur3 directionLampe=positionLampe.moins(n.point);
					directionLampe.normaliser();
					Vecteur3 pointDepartOmbre=n.point.plus(directionLampe.fois(0.0001));
										
					intensite=Math.min(255,sphereEnglobante[surfaceIndex].rayon*550*Math.max(0,normale.produitScalaire(directionLampe))/n.point.moins(positionLampe).norme());
					fileTriangles.head=null;
					double speculaire=0;
					if (TD56.intersectionSurfaceRayonVraiFauxLineaire(surfaceScene[surfaceIndex],new Rayon(pointDepartOmbre,directionLampe))) intensite/=3.0;
					else {
						
						Vecteur3 rayonIncident=rayonCamera.direction.fois(-1.0);
						Vecteur3 composanteNormale=normale.fois(rayonIncident.produitScalaire(normale));
						Vecteur3 rayonReflechi=composanteNormale.plus(composanteNormale.moins(rayonIncident).fois(2.0));
						rayonReflechi.normaliser();
						double facteur=(Math.abs(directionLampe.produitScalaire(rayonReflechi)))*Math.max(0,normale.produitScalaire(directionLampe));
						speculaire=255*facteur*facteur*facteur*facteur;
					
					}
					if (rayonCamera.pointDeDepart.moins(pointSol).produitScalaire(normaleSol)<0) { intensite=20;speculaire=0; }
					intensite=Math.max(15,intensite);
					double R=Math.min(255,(intensite/255.0*couleurSurface[surfaceIndex].getRed())+speculaire);
					double G=Math.min(255,(intensite/255.0*couleurSurface[surfaceIndex].getGreen())+speculaire);
					double B=Math.min(255,(intensite/255.0*couleurSurface[surfaceIndex].getBlue())+speculaire);
					g.setColor(new Color((int)R,(int)G,(int)B));
					
				}
				else {
					
					Vecteur3 directionLampe=positionLampe.moins(pointIntersection);
					directionLampe.normaliser();
					Vecteur3 pointDepartOmbre=pointIntersection.plus(directionLampe.fois(0.0001));
										
					intensite=Math.min(255,sphereEnglobante[surfaceIndex].rayon*550*Math.max(0,normaleSol.produitScalaire(directionLampe))/pointIntersection.moins(positionLampe).norme());
					fileTriangles.head=null;
					if (TD56.intersectionSurfaceRayonVraiFauxLineaire(surfaceScene[surfaceIndex],new Rayon(pointDepartOmbre,directionLampe))) intensite/=3.0;
					if (rayonCamera.pointDeDepart.moins(pointSol).produitScalaire(normaleSol)<0) intensite=20;
					intensite=Math.max(25,intensite);
					g.setColor(new Color((int)(intensite/255.0*couleurSol.getRed()),(int)(intensite/255.0*couleurSol.getGreen()),(int)(intensite/255.0*couleurSol.getBlue())));
					
				}

				g.fillRect(x,y,pasRayTracing,pasRayTracing);

			}
			
		}
		
	}
	
	static void calculerCoordonneesAffichage(Vecteur3[] sommet, Vecteur3[] sommetProjete) {
	
		if (sommet==null) return;
		
		Vecteur3 PM=null;
		Vecteur3 CM=null;
		Vecteur3 PC=camera.cible.moins(camera.position);
		Vecteur3 ii=new Vecteur3(PC.y*camera.haut.z-PC.z*camera.haut.y,PC.z*camera.haut.x-PC.x*camera.haut.z,PC.x*camera.haut.y-PC.y*camera.haut.x);
		ii.normaliser();
		
		double PMPC=0.0;
		double PCNorme=PC.norme();
		double PCNorme2=PC.norme2();

		double Y=PCNorme*Math.tan(Math.toRadians(camera.fovy));
		double X=camera.aspect*Y;

		for (int i=0;i<sommet.length;i++) {
		
			if (sommet[i]==null) continue;
			
			PM=sommet[i].moins(camera.position);
			PMPC=PM.produitScalaire(PC);
			if (PMPC<=0) continue;
			
			double lambda=PCNorme2/PMPC;
			CM=sommet[i].moins(camera.cible);
		
			double a=lambda*CM.produitScalaire(ii);
			double b=lambda*CM.produitScalaire(camera.haut);
			
			sommetProjete[i].x=largeur/2.0*(1+a/X);
			sommetProjete[i].y=hauteur/2.0*(1-b/Y);
			sommetProjete[i].z=0.0;
			
		}
		
	}

	static Vecteur3[] calculerCoordonneesAffichage(FileTriangles file) {
	
		int longueurFile=0;
		NoeudFileTriangles noeudCourant=file.head;
	
		while (noeudCourant!=null) {
		
			longueurFile++;
			noeudCourant=noeudCourant.suivant;
			
		}
		
		if (longueurFile==0) return null;
		
		Vecteur3[] sommet=new Vecteur3[2*3*longueurFile];
		Vecteur3[] sommetProjete=new Vecteur3[2*3*longueurFile];
	
		int index=0;
		noeudCourant=file.head;
	
		while (noeudCourant!=null) {
		
			sommet[index+0]=noeudCourant.triangleA.a;
			sommet[index+1]=noeudCourant.triangleA.b;
			sommet[index+2]=noeudCourant.triangleA.c;
			
			sommetProjete[index+0]=new Vecteur3();
			sommetProjete[index+1]=new Vecteur3();
			sommetProjete[index+2]=new Vecteur3();
			
			if (noeudCourant.triangleB!=null) {
				
				sommet[index+3]=noeudCourant.triangleB.a;
				sommet[index+4]=noeudCourant.triangleB.b;
				sommet[index+5]=noeudCourant.triangleB.c;
				sommetProjete[index+3]=new Vecteur3();
				sommetProjete[index+4]=new Vecteur3();
				sommetProjete[index+5]=new Vecteur3();
			
			}
			
			index+=6;
			noeudCourant=noeudCourant.suivant;
			
		}

		calculerCoordonneesAffichage(sommet,sommetProjete);
		
		return sommetProjete;
		
	}

	static void dessinerSurface(Graphics g, int surfaceIndex) {
	
		for (int i=0;i<surfaceScene[surfaceIndex].triangle.length;i++) {

			g.drawLine((int)triangleAffichage[surfaceIndex][i].a.x,(int)triangleAffichage[surfaceIndex][i].a.y,(int)triangleAffichage[surfaceIndex][i].b.x,(int)triangleAffichage[surfaceIndex][i].b.y);
			g.drawLine((int)triangleAffichage[surfaceIndex][i].c.x,(int)triangleAffichage[surfaceIndex][i].c.y,(int)triangleAffichage[surfaceIndex][i].b.x,(int)triangleAffichage[surfaceIndex][i].b.y);
			g.drawLine((int)triangleAffichage[surfaceIndex][i].a.x,(int)triangleAffichage[surfaceIndex][i].a.y,(int)triangleAffichage[surfaceIndex][i].c.x,(int)triangleAffichage[surfaceIndex][i].c.y);
	
		}
		
	}
	
	static void dessinerTrianglesVerts(Graphics g, FileTriangles file) {
	
		if (file==null) return;
		if (file.head==null) return;
	
		Vecteur3[] sommetVert=calculerCoordonneesAffichage(file);
		
		g.setColor(Color.green);
		
		int i=0;
		
		for (i=0;i<sommetVert.length;i+=6) {
		
			g.drawLine((int)sommetVert[i+0].x,(int)sommetVert[i+0].y,(int)sommetVert[i+1].x,(int)sommetVert[i+1].y);
			g.drawLine((int)sommetVert[i+2].x,(int)sommetVert[i+2].y,(int)sommetVert[i+1].x,(int)sommetVert[i+1].y);
			g.drawLine((int)sommetVert[i+0].x,(int)sommetVert[i+0].y,(int)sommetVert[i+2].x,(int)sommetVert[i+2].y);

			if (sommetVert[i+3]!=null) {
				
				g.drawLine((int)sommetVert[i+3].x,(int)sommetVert[i+3].y,(int)sommetVert[i+4].x,(int)sommetVert[i+4].y);
				g.drawLine((int)sommetVert[i+5].x,(int)sommetVert[i+5].y,(int)sommetVert[i+4].x,(int)sommetVert[i+4].y);
				g.drawLine((int)sommetVert[i+3].x,(int)sommetVert[i+3].y,(int)sommetVert[i+5].x,(int)sommetVert[i+5].y);

			}
			
		}
		
	}

	static void dessinerPlan(Graphics g) {
	
		if (pointDemiEspace==null) return;
		
		Vecteur3 OX=normaleDemiEspace.produitVectoriel(new Vecteur3(1.0,0.0,0.0));
		Vecteur3 OY=normaleDemiEspace.produitVectoriel(OX);
		OX.normaliser();
		OY.normaliser();
		
		Vecteur3 PC=camera.cible.moins(camera.position);
		Vecteur3 ii=new Vecteur3();
		Vecteur3 CM=new Vecteur3();
		ii.x=PC.y*camera.haut.z-PC.z*camera.haut.y;
		ii.y=PC.z*camera.haut.x-PC.x*camera.haut.z;
		ii.z=PC.x*camera.haut.y-PC.y*camera.haut.x;
		ii.normaliser();
		
		double PCNorme=PC.norme();
		double PCNorme2=PC.norme2();

		double Y=PCNorme*Math.tan(Math.toRadians(camera.fovy));
		double X=camera.aspect*Y;
		double PMPC;
		double lambda;
		
		for (double i=-sphereEnglobante[0].rayon;i<=sphereEnglobante[0].rayon;i+=sphereEnglobante[0].rayon/35.0) {
		
			Vecteur3 pointA=(pointDemiEspace.plus(OX.fois(i)).plus(OY.fois(-sphereEnglobante[0].rayon)));
			PMPC=pointA.moins(camera.position).produitScalaire(PC);
			lambda=PCNorme2/PMPC;
			CM=pointA.moins(camera.cible);
		
			double aA=lambda*CM.produitScalaire(ii);
			double bA=lambda*CM.produitScalaire(camera.haut);
			
			int xA=(int)(largeur/2.0*(1+aA/X));
			int yA=(int)(hauteur/2.0*(1-bA/Y));
			
			Vecteur3 pointB=(pointDemiEspace.plus(OX.fois(i)).plus(OY.fois(sphereEnglobante[0].rayon)));
			PMPC=pointB.moins(camera.position).produitScalaire(PC);
			lambda=PCNorme2/PMPC;
			CM=pointB.moins(camera.cible);
		
			double aB=lambda*CM.produitScalaire(ii);
			double bB=lambda*CM.produitScalaire(camera.haut);
			
			int xB=(int)(largeur/2.0*(1+aB/X));
			int yB=(int)(hauteur/2.0*(1-bB/Y));

			g.drawLine(xA,yA,xB,yB);
			
		}
		
		for (double i=-sphereEnglobante[0].rayon;i<=sphereEnglobante[0].rayon;i+=sphereEnglobante[0].rayon/35.0) {
		
			Vecteur3 pointA=(pointDemiEspace.plus(OX.fois(-sphereEnglobante[0].rayon)).plus(OY.fois(i)));
			PMPC=pointA.moins(camera.position).produitScalaire(PC);
			lambda=PCNorme2/PMPC;
			CM=pointA.moins(camera.cible);
		
			double aA=lambda*CM.produitScalaire(ii);
			double bA=lambda*CM.produitScalaire(camera.haut);
			
			int xA=(int)(largeur/2.0*(1+aA/X));
			int yA=(int)(hauteur/2.0*(1-bA/Y));
			
			Vecteur3 pointB=(pointDemiEspace.plus(OX.fois(sphereEnglobante[0].rayon)).plus(OY.fois(i)));
			PMPC=pointB.moins(camera.position).produitScalaire(PC);
			lambda=PCNorme2/PMPC;
			CM=pointB.moins(camera.cible);
		
			double aB=lambda*CM.produitScalaire(ii);
			double bB=lambda*CM.produitScalaire(camera.haut);
			
			int xB=(int)(largeur/2.0*(1+aB/X));
			int yB=(int)(hauteur/2.0*(1-bB/Y));

			g.drawLine(xA,yA,xB,yB);
			
		}
		
		Vecteur3 pointA=pointDemiEspace.plus(normaleDemiEspace.fois(0.5*sphereEnglobante[0].rayon));
		PMPC=pointA.moins(camera.position).produitScalaire(PC);
		lambda=PCNorme2/PMPC;
		CM=pointA.moins(camera.cible);
	
		double aA=lambda*CM.produitScalaire(ii);
		double bA=lambda*CM.produitScalaire(camera.haut);
		
		int xA=(int)(largeur/2.0*(1+aA/X));
		int yA=(int)(hauteur/2.0*(1-bA/Y));
		
		PMPC=pointDemiEspace.moins(camera.position).produitScalaire(PC);
		lambda=PCNorme2/PMPC;
		CM=pointDemiEspace.moins(camera.cible);
	
		double aB=lambda*CM.produitScalaire(ii);
		double bB=lambda*CM.produitScalaire(camera.haut);
		
		int xB=(int)(largeur/2.0*(1+aB/X));
		int yB=(int)(hauteur/2.0*(1-bB/Y));

		g.drawLine(xA,yA,xB,yB);
			
	}
	
	static void dessinerSol(Graphics g) {
	
		if (pointSol==null) return;
		
		Vecteur3 OX=normaleSol.produitVectoriel(new Vecteur3(1.0,0.0,0.0));
		Vecteur3 OY=normaleSol.produitVectoriel(OX);
		OX.normaliser();
		OY.normaliser();
		
		Vecteur3 PC=camera.cible.moins(camera.position);
		Vecteur3 ii=new Vecteur3();
		Vecteur3 CM=new Vecteur3();
		ii.x=PC.y*camera.haut.z-PC.z*camera.haut.y;
		ii.y=PC.z*camera.haut.x-PC.x*camera.haut.z;
		ii.z=PC.x*camera.haut.y-PC.y*camera.haut.x;
		ii.normaliser();
		
		double PCNorme=PC.norme();
		double PCNorme2=PC.norme2();

		double Y=PCNorme*Math.tan(Math.toRadians(camera.fovy));
		double X=camera.aspect*Y;
		double PMPC;
		double lambda;
		
		for (double i=-sphereEnglobante[0].rayon;i<=sphereEnglobante[0].rayon+0.1;i+=sphereEnglobante[0].rayon/8.0) {
		
			Vecteur3 pointA=(pointSol.plus(OX.fois(i)).plus(OY.fois(-sphereEnglobante[0].rayon)));
			PMPC=pointA.moins(camera.position).produitScalaire(PC);
			lambda=PCNorme2/PMPC;
			CM=pointA.moins(camera.cible);
		
			double aA=lambda*CM.produitScalaire(ii);
			double bA=lambda*CM.produitScalaire(camera.haut);
			
			int xA=(int)(largeur/2.0*(1+aA/X));
			int yA=(int)(hauteur/2.0*(1-bA/Y));
			
			Vecteur3 pointB=(pointSol.plus(OX.fois(i)).plus(OY.fois(sphereEnglobante[0].rayon)));
			PMPC=pointB.moins(camera.position).produitScalaire(PC);
			lambda=PCNorme2/PMPC;
			CM=pointB.moins(camera.cible);
		
			double aB=lambda*CM.produitScalaire(ii);
			double bB=lambda*CM.produitScalaire(camera.haut);
			
			int xB=(int)(largeur/2.0*(1+aB/X));
			int yB=(int)(hauteur/2.0*(1-bB/Y));

			g.drawLine(xA,yA,xB,yB);
			
		}
		
		for (double i=-sphereEnglobante[0].rayon;i<=sphereEnglobante[0].rayon+0.1;i+=sphereEnglobante[0].rayon/8.0) {
		
			Vecteur3 pointA=(pointSol.plus(OX.fois(-sphereEnglobante[0].rayon)).plus(OY.fois(i)));
			PMPC=pointA.moins(camera.position).produitScalaire(PC);
			lambda=PCNorme2/PMPC;
			CM=pointA.moins(camera.cible);
		
			double aA=lambda*CM.produitScalaire(ii);
			double bA=lambda*CM.produitScalaire(camera.haut);
			
			int xA=(int)(largeur/2.0*(1+aA/X));
			int yA=(int)(hauteur/2.0*(1-bA/Y));
			
			Vecteur3 pointB=(pointSol.plus(OX.fois(sphereEnglobante[0].rayon)).plus(OY.fois(i)));
			PMPC=pointB.moins(camera.position).produitScalaire(PC);
			lambda=PCNorme2/PMPC;
			CM=pointB.moins(camera.cible);
		
			double aB=lambda*CM.produitScalaire(ii);
			double bB=lambda*CM.produitScalaire(camera.haut);
			
			int xB=(int)(largeur/2.0*(1+aB/X));
			int yB=(int)(hauteur/2.0*(1-bB/Y));

			g.drawLine(xA,yA,xB,yB);
			
		}
			
	}
	
	static void dessinerRayon(Graphics g) {
	
		if (rayonCamera==null) return;
		
		Vecteur3 PC=camera.cible.moins(camera.position);
		Vecteur3 ii=new Vecteur3();
		Vecteur3 CM=new Vecteur3();
		ii.x=PC.y*camera.haut.z-PC.z*camera.haut.y;
		ii.y=PC.z*camera.haut.x-PC.x*camera.haut.z;
		ii.z=PC.x*camera.haut.y-PC.y*camera.haut.x;
		ii.normaliser();
		
		double PCNorme=PC.norme();
		double PCNorme2=PC.norme2();

		double Y=PCNorme*Math.tan(Math.toRadians(camera.fovy));
		double X=camera.aspect*Y;
		double PMPC;
		double lambda;
	
		Vecteur3 pointA=rayonCamera.pointDeDepart;
		PMPC=pointA.moins(camera.position).produitScalaire(PC);
		lambda=PCNorme2/PMPC;
		CM=pointA.moins(camera.cible);
	
		double aA=lambda*CM.produitScalaire(ii);
		double bA=lambda*CM.produitScalaire(camera.haut);
		
		int xA=(int)(largeur/2.0*(1+aA/X));
		int yA=(int)(hauteur/2.0*(1-bA/Y));
		
		Vecteur3 pointB=(pointA.plus(rayonCamera.direction.fois(2.0*sphereEnglobante[0].rayon)));
		PMPC=pointB.moins(camera.position).produitScalaire(PC);
		lambda=PCNorme2/PMPC;
		CM=pointB.moins(camera.cible);
	
		double aB=lambda*CM.produitScalaire(ii);
		double bB=lambda*CM.produitScalaire(camera.haut);
		
		int xB=(int)(largeur/2.0*(1+aB/X));
		int yB=(int)(hauteur/2.0*(1-bB/Y));

		g.drawLine(xA,yA,xB,yB);
		g.drawOval(xA-10,yA-10,20,20);
		
	}
	
	static void dessinerPolyedre(Graphics g) {
	
		if (pointPolyedre==null) return;
		
		for (int face=0;face<nFacesPolyedre;face++) {
			
			Vecteur3 OX=normalePolyedre[face].produitVectoriel(new Vecteur3(1.0,0.0,0.0));
			Vecteur3 OY=normalePolyedre[face].produitVectoriel(OX);
			OX.normaliser();
			OY.normaliser();
			
			Vecteur3 PC=camera.cible.moins(camera.position);
			Vecteur3 ii=new Vecteur3();
			Vecteur3 CM=new Vecteur3();
			ii.x=PC.y*camera.haut.z-PC.z*camera.haut.y;
			ii.y=PC.z*camera.haut.x-PC.x*camera.haut.z;
			ii.z=PC.x*camera.haut.y-PC.y*camera.haut.x;
			ii.normaliser();
			
			double PCNorme=PC.norme();
			double PCNorme2=PC.norme2();

			double Y=PCNorme*Math.tan(Math.toRadians(camera.fovy));
			double X=camera.aspect*Y;
			double PMPC;
			double lambda;
			
			for (double i=-sphereEnglobante[0].rayon;i<=sphereEnglobante[0].rayon;i+=sphereEnglobante[0].rayon/50.0) {

				for (double j=-sphereEnglobante[0].rayon;j<=sphereEnglobante[0].rayon;j+=sphereEnglobante[0].rayon/50.0) {
				
					Vecteur3 pointA=(pointPolyedre[face].plus(OX.fois(i)).plus(OY.fois(j)));
					
					PMPC=pointA.moins(camera.position).produitScalaire(PC);
					lambda=PCNorme2/PMPC;
					CM=pointA.moins(camera.cible);
				
					double aA=lambda*CM.produitScalaire(ii);
					double bA=lambda*CM.produitScalaire(camera.haut);
					
					int xA=(int)(largeur/2.0*(1+aA/X));
					int yA=(int)(hauteur/2.0*(1-bA/Y));
					
					Vecteur3 pointB=(pointPolyedre[face].plus(OX.fois(i)).plus(OY.fois(j+sphereEnglobante[0].rayon/50.0)));
					PMPC=pointB.moins(camera.position).produitScalaire(PC);
					lambda=PCNorme2/PMPC;
					CM=pointB.moins(camera.cible);
				
					double aB=lambda*CM.produitScalaire(ii);
					double bB=lambda*CM.produitScalaire(camera.haut);
					
					int xB=(int)(largeur/2.0*(1+aB/X));
					int yB=(int)(hauteur/2.0*(1-bB/Y));

					boolean appartenanceA=true;
					boolean appartenanceB=true;
					for (int k=0;k<nFacesPolyedre;k++) if (k!=face) if (pointA.moins(pointPolyedre[k]).produitScalaire(normalePolyedre[k])>0) appartenanceA=false;
					for (int k=0;k<nFacesPolyedre;k++) if (k!=face) if (pointB.moins(pointPolyedre[k]).produitScalaire(normalePolyedre[k])>0) appartenanceB=false;
					
					if (appartenanceA^appartenanceB) g.drawOval((int)(0.5*(double)(xA+xB)-1),(int)(0.5*(double)(yA+yB)-1),2,2);
					
				}

			}
						
			for (double i=-sphereEnglobante[0].rayon;i<=sphereEnglobante[0].rayon;i+=sphereEnglobante[0].rayon/50.0) {

				for (double j=-sphereEnglobante[0].rayon;j<=sphereEnglobante[0].rayon;j+=sphereEnglobante[0].rayon/50.0) {
				
					Vecteur3 pointA=(pointPolyedre[face].plus(OX.fois(j)).plus(OY.fois(i)));
					PMPC=pointA.moins(camera.position).produitScalaire(PC);
					lambda=PCNorme2/PMPC;
					CM=pointA.moins(camera.cible);
				
					double aA=lambda*CM.produitScalaire(ii);
					double bA=lambda*CM.produitScalaire(camera.haut);
					
					int xA=(int)(largeur/2.0*(1+aA/X));
					int yA=(int)(hauteur/2.0*(1-bA/Y));
					
					Vecteur3 pointB=(pointPolyedre[face].plus(OX.fois(j+sphereEnglobante[0].rayon/50.0)).plus(OY.fois(i)));
					PMPC=pointB.moins(camera.position).produitScalaire(PC);
					lambda=PCNorme2/PMPC;
					CM=pointB.moins(camera.cible);
				
					double aB=lambda*CM.produitScalaire(ii);
					double bB=lambda*CM.produitScalaire(camera.haut);
					
					int xB=(int)(largeur/2.0*(1+aB/X));
					int yB=(int)(hauteur/2.0*(1-bB/Y));

					boolean appartenanceA=true;
					boolean appartenanceB=true;
					for (int k=0;k<nFacesPolyedre;k++) if (k!=face) if (pointA.moins(pointPolyedre[k]).produitScalaire(normalePolyedre[k])>0) appartenanceA=false;
					for (int k=0;k<nFacesPolyedre;k++) if (k!=face) if (pointB.moins(pointPolyedre[k]).produitScalaire(normalePolyedre[k])>0) appartenanceB=false;
					
					if (appartenanceA^appartenanceB) g.drawOval((int)(0.5*(double)(xA+xB)-1),(int)(0.5*(double)(yA+yB)-1),2,2);
					
				}

			}
						
		}
		
	}
	
	static void lireSurface(int surfaceIndex, String nomSurface) {
	
		surfaceScene[surfaceIndex]=null;
		
		// cette fonction cree une surface a partir de sa description dans un fichier
		
		java.io.File f=new java.io.File(nomSurface);

		try {
					
			int nSommets=0;
			int nTriangles=0;
			
			FileReader r = new FileReader(f);
       		BufferedReader br = new BufferedReader(r);
			StreamTokenizer st = new StreamTokenizer(br);
			st.eolIsSignificant(true);
			
			st.nextToken();nSommets=(int)st.nval;st.nextToken();
			st.nextToken();nTriangles=(int)st.nval;st.nextToken();
			System.out.println("Chargement de la surface "+nomSurface+" : "+nSommets+" sommets et "+nTriangles+" triangles."); 
			
			surfaceScene[surfaceIndex]=new Surface();
			surfaceScene[surfaceIndex].sommet=new Vecteur3[nSommets];
			surfaceScene[surfaceIndex].triangle=new Triangle[nTriangles];
			
			sommetAffichage[surfaceIndex]=new Vecteur3[nSommets];
			triangleAffichage[surfaceIndex]=new Triangle[nTriangles];
			
			for (int i=0;i<nSommets;i++) {
			
				surfaceScene[surfaceIndex].sommet[i]=new Vecteur3();
				sommetAffichage[surfaceIndex][i]=new Vecteur3();
				
				st.nextToken();
				st.nextToken();surfaceScene[surfaceIndex].sommet[i].x=st.nval;
				st.nextToken();surfaceScene[surfaceIndex].sommet[i].y=st.nval;
				st.nextToken();surfaceScene[surfaceIndex].sommet[i].z=st.nval;
				st.nextToken();
				
			}
			
			for (int i=0;i<nTriangles;i++) {
			
				surfaceScene[surfaceIndex].triangle[i]=new Triangle();
				triangleAffichage[surfaceIndex][i]=new Triangle();
				
				st.nextToken();
				st.nextToken();surfaceScene[surfaceIndex].triangle[i].a=surfaceScene[surfaceIndex].sommet[(int)st.nval];triangleAffichage[surfaceIndex][i].a=sommetAffichage[surfaceIndex][(int)st.nval];
				st.nextToken();surfaceScene[surfaceIndex].triangle[i].b=surfaceScene[surfaceIndex].sommet[(int)st.nval];triangleAffichage[surfaceIndex][i].b=sommetAffichage[surfaceIndex][(int)st.nval];
				st.nextToken();surfaceScene[surfaceIndex].triangle[i].c=surfaceScene[surfaceIndex].sommet[(int)st.nval];triangleAffichage[surfaceIndex][i].c=sommetAffichage[surfaceIndex][(int)st.nval];
				st.nextToken();
				
			}
			
		}
		catch (Exception e) {

	        	
			e.printStackTrace();
	        
		
		}
	
	}

	static void transformer(int surfaceIndex, Matrice34 transformation) {
	
		// calcul de la transformation totale, depuis le chargement de l'objet
		
		transformationSurface[surfaceIndex]=transformation.fois(transformationSurface[surfaceIndex]);
		
		// transformation des sommets
		
		for (int i=0;i<surfaceScene[surfaceIndex].sommet.length;i++) {
		
			double x=transformation.m[0][0]*surfaceScene[surfaceIndex].sommet[i].x+transformation.m[0][1]*surfaceScene[surfaceIndex].sommet[i].y+transformation.m[0][2]*surfaceScene[surfaceIndex].sommet[i].z+transformation.m[0][3];
			double y=transformation.m[1][0]*surfaceScene[surfaceIndex].sommet[i].x+transformation.m[1][1]*surfaceScene[surfaceIndex].sommet[i].y+transformation.m[1][2]*surfaceScene[surfaceIndex].sommet[i].z+transformation.m[1][3];
			double z=transformation.m[2][0]*surfaceScene[surfaceIndex].sommet[i].x+transformation.m[2][1]*surfaceScene[surfaceIndex].sommet[i].y+transformation.m[2][2]*surfaceScene[surfaceIndex].sommet[i].z+transformation.m[2][3];
		
			surfaceScene[surfaceIndex].sommet[i].x=x;
			surfaceScene[surfaceIndex].sommet[i].y=y;
			surfaceScene[surfaceIndex].sommet[i].z=z;
			
		}
	
		// transformation de la hierarchie de spheres englobantes
		
		transformerArbreSpheres(surfaceIndex,transformation,surfaceScene[surfaceIndex].arbreSpheres);
		
	}

	static void transformerArbreSpheres(int surfaceIndex, Matrice34 transformation, ArbreSpheres noeud) {
		
		if (noeud==null) return;
		
		double x=transformation.m[0][0]*noeud.sphere.centre.x+transformation.m[0][1]*noeud.sphere.centre.y+transformation.m[0][2]*noeud.sphere.centre.z+transformation.m[0][3];
		double y=transformation.m[1][0]*noeud.sphere.centre.x+transformation.m[1][1]*noeud.sphere.centre.y+transformation.m[1][2]*noeud.sphere.centre.z+transformation.m[1][3];
		double z=transformation.m[2][0]*noeud.sphere.centre.x+transformation.m[2][1]*noeud.sphere.centre.y+transformation.m[2][2]*noeud.sphere.centre.z+transformation.m[2][3];
	
		noeud.sphere.centre.x=x;
		noeud.sphere.centre.y=y;
		noeud.sphere.centre.z=z;
		
		transformerArbreSpheres(surfaceIndex,transformation,noeud.filsGauche);
		transformerArbreSpheres(surfaceIndex,transformation,noeud.filsDroit);
		
	}

	static void construireArbreSpheresComplet(Surface surface) {
	
		FileTriangles file=new FileTriangles();
		for (int i=0;i<surface.triangle.length;i++) file.insererTriangles(surface.triangle[i],null,null,0.0);

		surface.arbreSpheres=TD56.construireArbreSpheresPourTrianglesDansFile(file);
	
	}

	static void initialisationScene() {
	
		// allocation memoire
		
		sphereEnglobante=new Sphere[2];
		sommetAffichage=new Vecteur3[2][];
		triangleAffichage=new Triangle[2][];
		couleurSurface=new Color[2];
		transformationSurface=new Matrice34[2];
		 
		// initialisation geometrie
		
		surfaceScene=new Surface[2];
		
		lireSurface(0,"theiere.cdo");
		lireSurface(1,"lapin.cdo");
		
		sphereEnglobante[0]=new Sphere(surfaceScene[0].sommet); // sphere englobante centree sur la surface 0
		sphereEnglobante[1]=new Sphere(surfaceScene[1].sommet); // sphere englobante centree sur la surface 1
		
		couleurSurface[0]=couleurSurface0;
		couleurSurface[1]=couleurSurface1;
		
		transformationSurface[0]=new Matrice34();
		transformationSurface[1]=new Matrice34();
		
		construireArbreSpheresComplet(surfaceScene[0]);
		construireArbreSpheresComplet(surfaceScene[1]);

		// camera
		
		camera=new Camera();
		camera.cible=new Vecteur3(sphereEnglobante[0].centre.x,sphereEnglobante[0].centre.y,sphereEnglobante[0].centre.z);
		camera.position=new Vecteur3(camera.cible.x,camera.cible.y,camera.cible.z);
		camera.position.z+=2.0*sphereEnglobante[0].rayon;
		camera.haut=new Vecteur3(0,1,0);
		camera.fovy=30;
		camera.aspect=(double)largeur/hauteur;

		calculerCoordonneesAffichage(surfaceScene[0].sommet,sommetAffichage[0]);
		calculerCoordonneesAffichage(surfaceScene[1].sommet,sommetAffichage[1]);
		
		// sol
		
		pointSol=camera.cible.moins(camera.haut.fois(sphereEnglobante[0].rayon*0.8));
		normaleSol=new Vecteur3(0,1,0);
		
		// detection de collisions
		
		pointPolyedre=new Vecteur3[20];
		normalePolyedre=new Vecteur3[20];
				
		// ray-tracing
		
		rayonCamera=new Rayon();
		rayonCamera.pointDeDepart=new Vecteur3();
		rayonCamera.direction=new Vecteur3();
		positionLampe=camera.position.plus(new Vecteur3(-1.5*sphereEnglobante[0].rayon,2.0*sphereEnglobante[0].rayon,0));
		
		fileTriangles=new FileTriangles();
		
	}
	
    static void demarrer() {
		
		initialisationScene();
			
		final Label label1 = new Label ("Sommets : "+surfaceScene[0].sommet.length);
		final Label label2 = new Label ("Triangles : "+surfaceScene[0].triangle.length);
		final Label label3 = new Label ("  Niveau dessin spheres : "+niveauSpheres);
		final Label label4 = new Label ("  Pas dessin spheres : "+pasSpheres);
		final Label label5 = new Label ("Pas ray-tracing : "+pasRayTracing);
		final Label labelTemps = new Label ("Temps de calcul : 0 ms");

		label1.setAlignment (Label.RIGHT);
		label2.setAlignment (Label.RIGHT);
		label3.setAlignment (Label.LEFT);
		label4.setAlignment (Label.LEFT);
		label5.setAlignment (Label.RIGHT);

		final Scrollbar barre3 = new Scrollbar (Scrollbar.HORIZONTAL,niveauSpheres, 1, 0, 20);
		final Scrollbar barre4 = new Scrollbar (Scrollbar.HORIZONTAL,pasSpheres, 1, 1, 91);
		final Scrollbar barre5 = new Scrollbar (Scrollbar.HORIZONTAL,pasRayTracing, 1, 1, 20);

		final Button bouton1=new Button("Intersection plan V/F");
		final Button bouton2=new Button("Intersection plan");
		final Button bouton3=new Button("Intersection polyedre");
		final Button bouton4=new Button("Minimum");
		final Button bouton5=new Button("Intersection rayon V/F");
		final Button bouton6=new Button("Intersection rayon");
		final Button bouton7=new Button("Fil de fer");
		final Button bouton8=new Button("Ray-trace Mono");
		final Button bouton9=new Button("Ray-trace Normal");
		final Button bouton10=new Button("Ray-trace Lineaire");
		final Button bouton11=new Button("Echanger surfaces");
		final Button bouton12=new Button("Intersection surface / surface");
		final Button bouton13=new Button("Rotation surface");
		final Button bouton14=new Button("Ajuster sol");
		final Button bouton15=new Button("Annuler rotation");

		final Checkbox item1 = new Checkbox ("Afficher arbre", afficherArbre);
		
		final Canvas canevas = new Canvas () {

			static final long serialVersionUID = 0;
			
			public void update(Graphics g){
				
				paint(g);
			
			}
						
			public void paint (Graphics g) {

				afficherArbre=item1.getState();
				
				resetBuffer();
			 	
				if (modeVisualisation>=8&&modeVisualisation<=10) paintBuffer(g);
				else {
				
					if (graphics2!=null) paintBuffer(graphics2);
					if (g!=null) g.drawImage(offscreen,0,0,this);
	            
	            }
	            
		    }

			public void paintBuffer (Graphics g) {
			
				g.setColor (Color.WHITE);
				if (modeVisualisation<8||modeVisualisation>10) g.fillRect(0, 0, largeur, hauteur);
				
				if (modeVisualisation!=12) {
				
					label1.setText("Sommets : "+surfaceScene[0].sommet.length);
					label2.setText("Triangles : "+surfaceScene[0].triangle.length);

				}
				
				if (modeVisualisation==8) {
				
					long t0=System.currentTimeMillis();
					rayTraceSurfaceMonochrome(g,0);
					long t1=System.currentTimeMillis();
					labelTemps.setText("Temps de calcul : "+(t1-t0)+" ms");

				}
				else if (modeVisualisation==9) {
				
					long t0=System.currentTimeMillis();
					rayTraceSurface(g,0);
					long t1=System.currentTimeMillis();
					labelTemps.setText("Temps de calcul : "+(t1-t0)+" ms");
					
				}
				else if (modeVisualisation==10) {
				
					long t0=System.currentTimeMillis();
					rayTraceSurfaceLineaire(g,0);
					long t1=System.currentTimeMillis();
					labelTemps.setText("Temps de calcul : "+(t1-t0)+" ms");
					
				}
				else {
					
					if (modeVisualisation==1&&intersectionSurfaceMur) g.setColor(Color.green);
					else if (modeVisualisation==5&&intersectionSurfaceRayonCamera) g.setColor(Color.green);
					else g.setColor(Color.black);

					dessinerSurface(g,0);
					g.setColor(Color.black);
					if (modeVisualisation==12) dessinerSurface(g,1);
					dessinerSol(g);
					
					if (afficherArbre) { 
						
						dessinerArbreSpheres(g,surfaceScene[0].arbreSpheres,0);
						if (modeVisualisation==12) dessinerArbreSpheres(g,surfaceScene[1].arbreSpheres,0);
						
					}
					
					if (modeVisualisation==1) dessinerPlan(g);
					if (modeVisualisation==2) dessinerPlan(g);
					if (modeVisualisation==3) dessinerPolyedre(g);
					if (modeVisualisation==5) dessinerRayon(g);
					if (modeVisualisation==6) dessinerRayon(g);
					
					dessinerTrianglesVerts(g,fileTriangles);
					
				}
				
			}	
				
			public void resetBuffer() {
	
				if(graphics2!=null){
				
					graphics2.dispose();
					graphics2=null;
					
				}
				
				if(offscreen!=null){
				
					offscreen.flush();
					offscreen=null;
					
				}
				
				System.gc();
				offscreen = bas.createImage(largeur,hauteur);
				graphics2= offscreen.getGraphics();
				
			}

		};

		canevas.setBackground (Color.WHITE);
		canevas.setSize (largeur,hauteur);

		bouton1.addActionListener (new ActionListener () {

			public void actionPerformed (ActionEvent ae) {

				modeVisualisation=1;
				
				fileTriangles.head=null;
				normaleDemiEspace=new Vecteur3(0.5*(rnd.nextDouble()-0.5),0.5*(rnd.nextDouble()-0.5),0.5*(rnd.nextDouble()-0.5));
				normaleDemiEspace.normaliser();

				double distance=sphereEnglobante[0].rayon*1.1*rnd.nextDouble();
				pointDemiEspace=sphereEnglobante[0].centre.moins(normaleDemiEspace.fois(distance));
				
				long t0=System.currentTimeMillis();
				intersectionSurfaceMur=TD56.intersectionSurfaceMurVraiFaux(surfaceScene[0].arbreSpheres,pointDemiEspace,normaleDemiEspace);
				long t1=System.currentTimeMillis();
				labelTemps.setText("Temps de calcul : "+(t1-t0)+" ms");
				canevas.repaint();

			}

		});

		bouton2.addActionListener (new ActionListener () {

			public void actionPerformed (ActionEvent ae) {
				
				modeVisualisation=2;
				
				normaleDemiEspace=new Vecteur3(0.5*(rnd.nextDouble()-0.5),0.5*(rnd.nextDouble()-0.5),0.5*(rnd.nextDouble()-0.5));
				normaleDemiEspace.normaliser();

				double distance=sphereEnglobante[0].rayon*0.8*rnd.nextDouble();
				pointDemiEspace=sphereEnglobante[0].centre.moins(normaleDemiEspace.fois(distance));
				
				fileTriangles.head=null;
				long t0=System.currentTimeMillis();
				TD56.intersectionSurfaceMur(surfaceScene[0].arbreSpheres,pointDemiEspace,normaleDemiEspace,fileTriangles);
				long t1=System.currentTimeMillis();
				labelTemps.setText("Temps de calcul : "+(t1-t0)+" ms");
				canevas.repaint();
				
			}

		});

		bouton3.addActionListener (new ActionListener () {

			public void actionPerformed (ActionEvent ae) {

				modeVisualisation=3;
				
				nFacesPolyedre=10+rnd.nextInt(4);
				
				Vecteur3 centrePolyedre=new Vecteur3(sphereEnglobante[0].centre.x+0.6*sphereEnglobante[0].rayon*2.0*(rnd.nextDouble()-0.5),sphereEnglobante[0].centre.y+0.6*sphereEnglobante[0].rayon*2.0*(rnd.nextDouble()-0.5),sphereEnglobante[0].centre.z+0.6*sphereEnglobante[0].rayon*2.0*(rnd.nextDouble()-0.5));
				for (int i=0;i<nFacesPolyedre;i++) {
					
					normalePolyedre[i]=new Vecteur3((rnd.nextDouble()-0.5),(rnd.nextDouble()-0.5),(rnd.nextDouble()-0.5));
					normalePolyedre[i].normaliser();

					double distance=sphereEnglobante[0].rayon*0.6*rnd.nextDouble();
					pointPolyedre[i]=centrePolyedre.plus(normalePolyedre[i].fois(distance));
					
				}
				
				fileTriangles.head=null;
				long t0=System.currentTimeMillis();
				TD56.intersectionSurfacePolyedre(surfaceScene[0].arbreSpheres,nFacesPolyedre,pointPolyedre,normalePolyedre,fileTriangles);
				long t1=System.currentTimeMillis();
				labelTemps.setText("Temps de calcul : "+(t1-t0)+" ms");
				canevas.repaint();
			
			}

		});

		bouton4.addActionListener (new ActionListener () {

			public void actionPerformed (ActionEvent ae) {
				
				modeVisualisation=2;
				
				normaleDemiEspace=new Vecteur3(0.5*(rnd.nextDouble()-0.5),0.5*(rnd.nextDouble()-0.5),0.5*(rnd.nextDouble()-0.5));
				normaleDemiEspace.normaliser();
				pointDemiEspace=new Vecteur3(surfaceScene[0].sommet[0].x,surfaceScene[0].sommet[0].y,surfaceScene[0].sommet[0].z);

				fileTriangles.head=null;
				long t0=System.currentTimeMillis();
				TD56.minimumSurface(surfaceScene[0].arbreSpheres,pointDemiEspace,normaleDemiEspace);
				long t1=System.currentTimeMillis();
				labelTemps.setText("Temps de calcul : "+(t1-t0)+" ms");
				canevas.repaint();
				
			}
		
		});

		bouton5.addActionListener (new ActionListener () {

			public void actionPerformed (ActionEvent ae) {
				
				modeVisualisation=5;
				
				rayonCamera.pointDeDepart=new Vecteur3(sphereEnglobante[0].centre.x+0.8*sphereEnglobante[0].rayon*2.0*(rnd.nextDouble()-0.5),sphereEnglobante[0].centre.y+0.8*sphereEnglobante[0].rayon*2.0*(rnd.nextDouble()-0.5),sphereEnglobante[0].centre.z+0.8*sphereEnglobante[0].rayon*2.0*(rnd.nextDouble()-0.5));
				rayonCamera.direction=new Vecteur3(0.5*(rnd.nextDouble()-0.5),0.5*(rnd.nextDouble()-0.5),0.5*(rnd.nextDouble()-0.5));
				rayonCamera.direction.normaliser();
				
				fileTriangles.head=null;
				long t0=System.currentTimeMillis();
				intersectionSurfaceRayonCamera=TD56.intersectionSurfaceRayonVraiFaux(surfaceScene[0].arbreSpheres,rayonCamera);
				long t1=System.currentTimeMillis();
				labelTemps.setText("Temps de calcul : "+(t1-t0)+" ms");
				canevas.repaint();
				
			}

		});

		bouton6.addActionListener (new ActionListener () {

			public void actionPerformed (ActionEvent ae) {
				
				modeVisualisation=6;
				
				rayonCamera.pointDeDepart=new Vecteur3(sphereEnglobante[0].centre.x+0.8*sphereEnglobante[0].rayon*2.0*(rnd.nextDouble()-0.5),sphereEnglobante[0].centre.y+0.8*sphereEnglobante[0].rayon*2.0*(rnd.nextDouble()-0.5),sphereEnglobante[0].centre.z+0.8*sphereEnglobante[0].rayon*2.0*(rnd.nextDouble()-0.5));
				rayonCamera.direction=new Vecteur3(0.5*(rnd.nextDouble()-0.5),0.5*(rnd.nextDouble()-0.5),0.5*(rnd.nextDouble()-0.5));
				rayonCamera.direction.normaliser();
				
				fileTriangles.head=null;
				long t0=System.currentTimeMillis();
				TD56.intersectionSurfaceRayon(surfaceScene[0].arbreSpheres,rayonCamera,fileTriangles);
				long t1=System.currentTimeMillis();
				labelTemps.setText("Temps de calcul : "+(t1-t0)+" ms");
				canevas.repaint();
				
			}

		});

		bouton7.addActionListener (new ActionListener () {

			public void actionPerformed (ActionEvent ae) {

				modeVisualisation=0;
				item1.setState(false);
				afficherArbre=false;
								
				canevas.repaint();

			}

		});

		bouton8.addActionListener (new ActionListener () {

			public void actionPerformed (ActionEvent ae) {
				
				modeVisualisation=8;
				canevas.repaint();
				
			}

		});

		bouton9.addActionListener (new ActionListener () {

			public void actionPerformed (ActionEvent ae) {
				
				modeVisualisation=9;
				canevas.repaint();
				
			}

		});

		bouton10.addActionListener (new ActionListener () {

			public void actionPerformed (ActionEvent ae) {
				
				modeVisualisation=10;
				canevas.repaint();
				
			}

		});

		bouton11.addActionListener (new ActionListener () {

			public void actionPerformed (ActionEvent ae) {

				modeVisualisation=0;
				Surface echangeSurface=surfaceScene[1];
				surfaceScene[1]=surfaceScene[0];
				surfaceScene[0]=echangeSurface;
				
				Vecteur3[] echangeSommet=sommetAffichage[1];
				sommetAffichage[1]=sommetAffichage[0];
				sommetAffichage[0]=echangeSommet;
				
				Triangle[] echangeTriangle=triangleAffichage[1];
				triangleAffichage[1]=triangleAffichage[0];
				triangleAffichage[0]=echangeTriangle;
				
				Color echangeCouleur=couleurSurface[1];
				couleurSurface[1]=couleurSurface[0];
				couleurSurface[0]=echangeCouleur;
				
				Matrice34 echangeTransformationSurface=transformationSurface[1];
				transformationSurface[1]=transformationSurface[0];
				transformationSurface[0]=echangeTransformationSurface;
				
				Sphere echangeSphereEnglobante=sphereEnglobante[1];
				sphereEnglobante[1]=sphereEnglobante[0];
				sphereEnglobante[0]=echangeSphereEnglobante;

				calculerCoordonneesAffichage(surfaceScene[0].sommet,sommetAffichage[0]);

				label1.setText("Sommets : "+surfaceScene[0].sommet.length);
				label2.setText("Triangles : "+surfaceScene[0].triangle.length);
				fileTriangles.head=null;
				canevas.repaint();
				
			}

		});

		bouton12.addActionListener (new ActionListener () {

			public void actionPerformed (ActionEvent ae) {
				
				modeVisualisation=12;

				Matrice34 inverse=transformationSurface[1].inverse();
				transformer(1,inverse);

				Vecteur3 axe=new Vecteur3();
				axe.x=rnd.nextDouble()-0.5;
				axe.y=rnd.nextDouble()-0.5;
				axe.z=rnd.nextDouble()-0.5;
				axe.normaliser();
				double angle=rnd.nextDouble();
				
				Matrice34 transformation=new Matrice34();
				double distance=sphereEnglobante[0].rayon*0.5+sphereEnglobante[0].rayon*rnd.nextDouble();
				Vecteur3 position=new Vecteur3(rnd.nextDouble()-0.5,rnd.nextDouble()-0.5,rnd.nextDouble()-0.5);
				position.normaliser();
				transformation.rotationDepuisAxeEtAngle(axe,angle);
				transformation.m[0][3]=position.x*distance;
				transformation.m[1][3]=position.y*distance;
				transformation.m[2][3]=position.z*distance;
				transformer(1,transformation);
				calculerCoordonneesAffichage(surfaceScene[1].sommet,sommetAffichage[1]);
				
				label1.setText("Sommets : "+surfaceScene[0].sommet.length+" / "+surfaceScene[1].sommet.length);
				label2.setText("Triangles : "+surfaceScene[0].triangle.length+" / "+surfaceScene[1].triangle.length);

				fileTriangles.head=null;
				long t0=System.currentTimeMillis();
				TD56.intersectionSurfaceSurface(surfaceScene[0].arbreSpheres,surfaceScene[1].arbreSpheres,fileTriangles);
				long t1=System.currentTimeMillis();
				labelTemps.setText("Temps de calcul : "+(t1-t0)+" ms");

				canevas.repaint();
				
			}

		});

		bouton13.addActionListener (new ActionListener () {

			public void actionPerformed (ActionEvent ae) {
				
				modeVisualisation=0;
	
				Vecteur3 axe=new Vecteur3();
				axe.x=rnd.nextDouble();
				axe.y=rnd.nextDouble();
				axe.z=rnd.nextDouble();
				axe.normaliser();
				double angle=10.0*rnd.nextDouble();
				Matrice34 transformation=new Matrice34();
				transformation.rotationDepuisAxeEtAngle(axe,angle);
				
				transformer(0,transformation);
				calculerCoordonneesAffichage(surfaceScene[0].sommet,sommetAffichage[0]);
				
				fileTriangles.head=null;
				canevas.repaint();
				
			}

		});

		bouton14.addActionListener (new ActionListener () {

			public void actionPerformed (ActionEvent ae) {
				
				pointSol=new Vecteur3(surfaceScene[0].sommet[0].x,surfaceScene[0].sommet[0].y,surfaceScene[0].sommet[0].z);

				fileTriangles.head=null;
				long t0=System.currentTimeMillis();
				TD56.minimumSurface(surfaceScene[0].arbreSpheres,pointSol,normaleSol);
				long t1=System.currentTimeMillis();
				labelTemps.setText("Temps de calcul : "+(t1-t0)+" ms");
				canevas.repaint();
				
			}
		
		});

		bouton15.addActionListener (new ActionListener () {

			public void actionPerformed (ActionEvent ae) {
				
				modeVisualisation=0;

				Matrice34 inverse=transformationSurface[0].inverse();
				transformer(0,inverse);
				calculerCoordonneesAffichage(surfaceScene[0].sommet,sommetAffichage[0]);
				
				fileTriangles.head=null;
				canevas.repaint();
				
			}

		});

		item1.addItemListener (new ItemListener () {
			public void itemStateChanged (ItemEvent ae) {
				if (modeVisualisation==8) modeVisualisation=0;
				if (modeVisualisation==9) modeVisualisation=0;
				if (modeVisualisation==10) modeVisualisation=0;
				canevas.repaint ();
			}
		});
	
		final Panel haut = new Panel ();

		haut.setLayout (new GridLayout (0, 5));

		haut.add (new Label ("Detection de collisions"));
		haut.add (new Label ());
		haut.add (new Label ());
		haut.add (new Label ());
		haut.add (new Label ());

		haut.add (bouton1);
		haut.add (bouton2);
		haut.add (bouton3);
		haut.add (bouton4);
		haut.add (bouton12);

		haut.add (new Label ("Ray-tracing"));
		haut.add (new Label ());
		haut.add (new Label ());
		haut.add (new Label ());
		haut.add (new Label ());

		haut.add (bouton5);
		haut.add (bouton6);
		haut.add (bouton8);
		haut.add (bouton10);
		haut.add (bouton9);

		haut.add (new Label ("Parametres d'affichage"));
		haut.add (new Label ());
		haut.add (new Label ());
		haut.add (new Label ());
		haut.add (new Label ());

		haut.add (item1);
		haut.add (new Label ());
		haut.add (new Label ());
		haut.add (new Label ());
		haut.add (new Label ());

		haut.add (barre3);
		haut.add (label3);
		haut.add (label5);
		haut.add (barre5);
		haut.add (bouton13);

		haut.add (barre4);
		haut.add (label4);
		haut.add (new Label ());
		haut.add (bouton14);
		haut.add (bouton15);

		haut.add (labelTemps);
		haut.add (label1);
		haut.add (label2);
		haut.add (bouton11);
		haut.add (bouton7);

		haut.setBackground(Color.gray);
		
		bas = new Panel ();
		bas.setSize (largeur,hauteur);
		bas.add (canevas);

		barre3.addAdjustmentListener (new AdjustmentListener () {
			public void adjustmentValueChanged (AdjustmentEvent al) {
				niveauSpheres=barre3.getValue();
				label3.setText ("  Niveau dessin spheres : "+niveauSpheres);
				canevas.repaint();
			}
			});

		barre4.addAdjustmentListener (new AdjustmentListener () {
			public void adjustmentValueChanged (AdjustmentEvent al) {
				pasSpheres=barre4.getValue();
				label4.setText("  Pas dessin spheres : "+pasSpheres);
				canevas.repaint();
			}
			});		

		barre5.addAdjustmentListener (new AdjustmentListener () {
			public void adjustmentValueChanged (AdjustmentEvent al) {
				pasRayTracing=barre5.getValue();
				label5.setText("Pas ray-tracing : "+pasRayTracing);
				canevas.repaint();
			}
			});		

		// interface souris
		
		canevas.addMouseListener(new MouseListener() {
		
			// evenements souris
			
			public void mouseClicked(MouseEvent e) {
				sourisCliquee=true;
				positionSourisXPrecedent=e.getX();
				positionSourisYPrecedent=e.getY();
			}

			public void mousePressed(MouseEvent e) {
				
				boutonSouris1=false;
				boutonSouris2=false;
				boutonSouris3=false;
				if (e.getButton()==MouseEvent.BUTTON1) boutonSouris1=true;
				if (e.getButton()==MouseEvent.BUTTON2) boutonSouris2=true;
				if (e.getButton()==MouseEvent.BUTTON3) boutonSouris3=true;
				
				positionSourisXPrecedent=e.getX();
				positionSourisYPrecedent=e.getY();
			}

			public void mouseReleased(MouseEvent e) {
				sourisCliquee=false;
			}

			public void mouseEntered(MouseEvent e) {

			}

			public void mouseExited(MouseEvent e) {

				
			}

		});

		canevas.addMouseMotionListener(new MouseMotionListener() {
		
			// evenements souris
			
			public void mouseDragged(MouseEvent e) {

				if (modeVisualisation==8) modeVisualisation=0;
				if (modeVisualisation==9) modeVisualisation=0;
				if (modeVisualisation==10) modeVisualisation=0;
				
				if (boutonSouris1) {
					
					Vecteur3 CP=camera.position.moins(camera.cible);
					Vecteur3 increment=camera.haut.fois(-0.02*(e.getX()-positionSourisXPrecedent)).produitVectoriel(CP);
					camera.position=camera.position.plus(increment);
					camera.position=camera.cible.plus(camera.position.moins(camera.cible).fois(CP.norme()/camera.position.moins(camera.cible).norme()));
					
					CP=camera.position.moins(camera.cible);
					Vecteur3 axe=camera.haut.produitVectoriel(CP);
					axe.normaliser();
					increment=axe.fois(-0.02*(e.getY()-positionSourisYPrecedent)).produitVectoriel(CP);
					camera.position=camera.position.plus(increment);
					camera.position=camera.cible.plus(camera.position.moins(camera.cible).fois(CP.norme()/camera.position.moins(camera.cible).norme()));
					CP=camera.position.moins(camera.cible);
					camera.haut=CP.produitVectoriel(axe);
					camera.haut.normaliser();
					
					positionSourisXPrecedent=e.getX();
					positionSourisYPrecedent=e.getY();
					calculerCoordonneesAffichage(surfaceScene[0].sommet,sommetAffichage[0]);
					if (modeVisualisation==12) calculerCoordonneesAffichage(surfaceScene[1].sommet,sommetAffichage[1]);
					canevas.repaint();
				
				}
				
				if (boutonSouris2) {
				
					Vecteur3 CP=camera.position.moins(camera.cible);
					double zoom=1.0+0.01*(e.getY()-positionSourisYPrecedent);
					CP=CP.fois(zoom);
					
					camera.position=camera.cible.plus(CP);
					
					positionSourisXPrecedent=e.getX();
					positionSourisYPrecedent=e.getY();
					calculerCoordonneesAffichage(surfaceScene[0].sommet,sommetAffichage[0]);
					if (modeVisualisation==12) calculerCoordonneesAffichage(surfaceScene[1].sommet,sommetAffichage[1]);
					canevas.repaint();
			
				}
				
				if (boutonSouris3) {
					
					Vecteur3 CP=camera.position.moins(camera.cible);
					Vecteur3 axe=camera.haut.produitVectoriel(CP);
					axe.normaliser();
					camera.position=camera.position.plus(axe.fois(-0.1*(e.getX()-positionSourisXPrecedent)));
					camera.position=camera.position.plus(camera.haut.fois(0.1*(e.getY()-positionSourisYPrecedent)));
					camera.cible=camera.cible.plus(axe.fois(-0.1*(e.getX()-positionSourisXPrecedent)));
					camera.cible=camera.cible.plus(camera.haut.fois(0.1*(e.getY()-positionSourisYPrecedent)));
					positionSourisXPrecedent=e.getX();
					positionSourisYPrecedent=e.getY();
					calculerCoordonneesAffichage(surfaceScene[0].sommet,sommetAffichage[0]);
					if (modeVisualisation==12) calculerCoordonneesAffichage(surfaceScene[1].sommet,sommetAffichage[1]);
					canevas.repaint();
				
				}
				
			}

			public void mouseMoved(MouseEvent e) {
			}

		});

		final Frame fenetre = new Frame ("La theiere et le lapin");

		fenetre.addWindowListener (new WindowAdapter () {
			public void windowClosing (WindowEvent we) {
				System.exit (0);
			}
			});
			
		fenetre.add (haut, BorderLayout.NORTH);
		fenetre.add (bas, BorderLayout.CENTER);
		fenetre.pack ();
		fenetre.setBounds(40,40,largeur,hauteur+haut.getHeight()+40);
		fenetre.setBackground(Color.white);
		fenetre.setForeground(Color.black);
		fenetre.setVisible (true);	
		offscreen = bas.createImage(largeur,hauteur);
		graphics2= offscreen.getGraphics();
		
	}

}

