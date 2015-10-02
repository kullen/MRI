package edu.auburn.cardiomri.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javafx.geometry.Point2D;
import toxi.geom.Spline2D;
import toxi.geom.Vec2D;
import edu.auburn.cardiomri.datastructure.Contour;
import edu.auburn.cardiomri.datastructure.Vector3d;

public final class ContourCalc {
    /**
     * Distance between each of the generated points
     */
    public static float SEPARATION_DISTANCE = 1.0f; // Distance between each of
                                                    // the generated points

    /**
     * Calculates the centroid by averaging the x,y coordinates in the list
     *
     * @param points List of points used to find the centroid
     * @return The centroid of the points
     */
    public static Vector3d calcCentroid(List<Vector3d> points) {
        if (points == null) {
            throw new NullPointerException("List cannot be null");
        }
        if (points.size() == 0) {
            throw new IllegalArgumentException(
                    "List must have at least 1 point");
        }

        double averageX = 0, averageY = 0;
        for (Vector3d p : points) {
            averageX += p.getX();
            averageY += p.getY();
        }
        averageX /= points.size();
        averageY /= points.size();

        Vector3d centroid = new Vector3d(averageX, averageY, 0);
        return centroid;
    }

    /**
     * Sort the points in clockwise order.
     *
     * @param points List of points to sort
     */
    public static void sortPoints(List<Vector3d> points) {
        if (points == null) {
            throw new NullPointerException("List cannot be null");
        }
        if (points.size() == 0) {
            return;
        }

        Vector3d centroid = ContourCalc.calcCentroid(points);

        Collections.sort(points, new Comparator<Vector3d>() {

            /**
             * Returns negative integer if point p1 is comes before point p2 in
             * the contour?
             */
            @Override
            public int compare(Vector3d p1, Vector3d p2) {
                double thetaP1 = Math.atan2(p1.getY() - centroid.getY(),
                        p1.getX() - centroid.getX());
                double thetaP2 = Math.atan2(p2.getY() - centroid.getY(),
                        p2.getX() - centroid.getX());
                double delta = thetaP2 - thetaP1;
                return (int) Math.signum(delta);
            }
        });
    }

    /**
     * Method to generate a list of Vector3d objects that represent the 
     * Bezier curve
     * 
     * @param controlPoints - points generated by user clicks
     * @param isClosed
     * @return a list of Vector3d objects which contains all the (x,y) coordinate information to draw bezier curves
     */
    
//    public static List<Vector3d> generate(List<Vector3d> controlPoints, boolean isClosed) {
//    	
//      if (controlPoints == null) {
//    	  throw new NullPointerException("List cannot be null");
//      }
//      if (controlPoints.size() < 3 ) {
//    	  return new Vector<Vector3d>(controlPoints);
//      }
//      
//      //final points that will make the contour
//      List<Vector3d> generatedPoints = new Vector<Vector3d>(); 
//      
//      //points generated by the genCurve() function to be inserted into generatedPoints
//      List<Vector3d> curvePoints = new Vector<Vector3d>();
//      
//      ContourCalc.sortPoints(controlPoints);
//      
//      for(int i = 0; i < controlPoints.size() - 1 ; i++) {
//    	  
//    	  //checks to make sure tension points that have already been calculated or moved by the user are not overwritten
//    	  if(controlPoints.get(i).getTensionX() == 0.0 && controlPoints.get(i).getTensionY() == 0.0) {
//    		  
//    		  //create tension point from two control points of a segment
//    		  Vector3d tensionPoint = getTensionPoint(controlPoints.get(i), controlPoints.get(i+1));
// 
//    		  //represent this tension point within the corresponding control point
//    		  controlPoints.get(i).setTensionX(tensionPoint.getX());
//    		  controlPoints.get(i).setTensionY(tensionPoint.getY());
//    	  }
//    	  
//    	  //generate points for every segment of the curve
//    	  curvePoints = genCurve(controlPoints.get(i), controlPoints.get(i + 1));
//    	  
//    	  //add the segment to the contour
//    	  generatedPoints.addAll(curvePoints);
//      }
//      
//      Vector3d tensionPoint = getTensionPoint(controlPoints.get(controlPoints.size() - 1), controlPoints.get(0));
//      
//      controlPoints.get(controlPoints.size() - 1).setTensionX(tensionPoint.getX());
//      controlPoints.get(controlPoints.size() - 1).setTensionY(tensionPoint.getY());
//      
//      //final curve from the last point to the initial point
//      curvePoints = genCurve(controlPoints.get(controlPoints.size() - 1), controlPoints.get(0));
//      
//      generatedPoints.addAll(curvePoints);
//      return generatedPoints;
//    }

    public static List<Vector3d> generate(List<Vector3d> controlPoints, boolean isClosed) {
    	
        if (controlPoints == null) {
      	  throw new NullPointerException("List cannot be null");
        }
        if (controlPoints.size() < 2 ) {
      	  return new Vector<Vector3d>(controlPoints);
        }
        
        //final points that will make the contour
        List<Vector3d> generatedPoints = new Vector<Vector3d>(); 
        
        //points generated by the genCurve() function to be inserted into generatedPoints
        List<Vector3d> curvePoints = new Vector<Vector3d>();
        
        ContourCalc.sortPoints(controlPoints);
        
        for(int i = 0; i < controlPoints.size() - 1 ; i++) {
      	  
      	  //checks to make sure tension points that have already been calculated or moved by the user are not overwritten
          //shouldn't need to check both tension points
      	  if(controlPoints.get(i).getTensionX() == 0.0 && controlPoints.get(i).getTensionY() == 0.0) {
      		  
      		  //create tension point from two control points of a segment
      		  List<Vector3d> tensionPoints = getTensionPoint(controlPoints.get(i), controlPoints.get(i+1));
   
      		  //represent this tension point within the corresponding control point
      		  controlPoints.get(i).setTensionX(tensionPoints.get(0).getX());
      		  controlPoints.get(i).setTensionY(tensionPoints.get(0).getY());
      		  controlPoints.get(i).setTensionX2(tensionPoints.get(1).getX());
      		  controlPoints.get(i).setTensionY2(tensionPoints.get(1).getY());   		  
      	  }      	  
      	  //generate points for every segment of the curve
      	  curvePoints = genCurve(controlPoints.get(i), controlPoints.get(i + 1));
      	  
      	  //add the segment to the contour
      	  generatedPoints.addAll(curvePoints);
        }
        if(controlPoints.get(controlPoints.size() -1).getTensionX() == 0.0 && controlPoints.get(controlPoints.size() - 1).getTensionY() == 0.0) {
	        
	        List<Vector3d> tensionPoints = getTensionPoint(controlPoints.get(controlPoints.size() - 1), controlPoints.get(0));
	        
	        controlPoints.get(controlPoints.size() - 1).setTensionX(tensionPoints.get(0).getX());
	        controlPoints.get(controlPoints.size() - 1).setTensionY(tensionPoints.get(0).getY());
	        controlPoints.get(controlPoints.size() - 1).setTensionX2(tensionPoints.get(1).getX());
	        controlPoints.get(controlPoints.size() - 1).setTensionY2(tensionPoints.get(1).getY());
        }
        //final curve from the last point to the initial point
        curvePoints = genCurve(controlPoints.get(controlPoints.size() - 1), controlPoints.get(0));
        
        generatedPoints.addAll(curvePoints);
        return generatedPoints;
        
    }
    
    /**
     * Compute the change in arc length if the given point is added to the contour.
     * 
     * @param contour
     * @param newPoint
     * @return
     */
    public static float getDeltaArcLength(Contour contour, Vector3d newPoint) {
        List<Vector3d> originalList = contour.getControlPoints();
        List<Vector3d> modifiedList = contour.getControlPoints();
        modifiedList.add(newPoint);

        Spline2D original = getSplineFromControlPoints(originalList,
                contour.isClosedCurve());
        original.getDecimatedVertices(SEPARATION_DISTANCE);
        Spline2D modified = getSplineFromControlPoints(modifiedList,
                contour.isClosedCurve());
        modified.getDecimatedVertices(SEPARATION_DISTANCE);

        return modified.getEstimatedArcLength()
                - original.getEstimatedArcLength();
    }

    private static Spline2D getSplineFromControlPoints(
            List<Vector3d> controlPoints, boolean isClosed) {
        ContourCalc.sortPoints(controlPoints);

        List<Vector3d> rawPoints = new Vector<Vector3d>(controlPoints);
        if (isClosed) {
            rawPoints.add(rawPoints.get(0));
        }

        Vec2D[] points = new Vec2D[rawPoints.size()];
        Vector3d point;
        for (int i = 0; i < rawPoints.size(); i++) {
            point = rawPoints.get(i);
            points[i] = new Vec2D((float) point.getX(),
                    (float) point.getY());
        }

        Spline2D spline = new Spline2D(points);
        return spline;
    }
    
    /**
     * Calculates the Bezier control point between two control points
     * to generate the Bezier curve. Can be modified later to adjust the curve
     * 
     * @param first control point 
     * @param second control point
     * @return new Vector3d object with the appropriate X, Y coordinate
     */
//    public static Vector3d getTensionPoint(Vector3d a, Vector3d b) {
//    	
//    	//calculates the distance vector
//    	double dX = b.getX() - a.getX();
//    	double dY = b.getY() - a.getY();
//    	
//    	//calculates the midpoint
//    	double mX = a.getX() + (dX / 2);
//    	double mY = a.getY() + (dY / 2);
//    	
//    	//calculates the normal vector
//    	double nX = -dY;
//    	double nY = dX;
//    	
//    	//calculates the control point for the bezier curve
//    	//at the moment we add 1/5 the distance, this can be modified to change the bezier curve
//
//    	double controlX = mX + (nX / 5);
//    	double controlY = mY + (nY / 5);
//    	
//    	Vector3d controlPoint = new Vector3d(controlX, controlY, 0);
//    	return controlPoint;
//    }
    
    public static List<Vector3d> getTensionPoint(Vector3d a, Vector3d b) {
    	
    	List<Vector3d> twoTensionPoints = new Vector<Vector3d>();
    	
    	//calculates the distance vector
    	double dX = b.getX() - a.getX();
    	double dY = b.getY() - a.getY();
    	
    	//calculates the first midpoint
    	double m1X = a.getX() + (dX / 3);
    	double m1Y = a.getY() + (dY / 3);
    	
    	//calculates the first midpoint
    	double m2X = a.getX() + (2 *(dX / 3));
    	double m2Y = a.getY() + (2 *(dY / 3));
    	
    	//calculates the second normal vector
    	double nX = -dY;
    	double nY = dX;
    	
    	//calculates the control point for the bezier curve

    	double controlX1 = m1X + (nX / 4);
    	double controlY1 = m1Y + (nY / 4);
    	Vector3d tensionPoint1 = new Vector3d(controlX1, controlY1, 0);
    	
    	double controlX2 = m2X + (nX / 4);
    	double controlY2 = m2Y + (nY / 4);
    	Vector3d tensionPoint2 = new Vector3d(controlX2, controlY2, 0);
    	
    	twoTensionPoints.add(tensionPoint1);
    	twoTensionPoints.add(tensionPoint2);
    	return twoTensionPoints;
    }
    
//    public static List<Vector3d> genCurve(Vector3d a, Vector3d b) {
//    	
//    	List<Vector3d> bezierPoints = new Vector<Vector3d>();
//    	
//    	//calculates the distance vector to the tension point 		   
// 		double distanceAX = a.getTensionX() - a.getX();
// 		double distanceAY = a.getTensionY() - a.getY();
// 		
// 		//calculates the distance vector to the second points from the tension point
// 		double distanceBX = b.getX() - a.getTensionX();
// 		double distanceBY = b.getY() - a.getTensionY();
// 		   
//     	for(int j = 0; j < 99; j++) {
//     	   
//     		//calculates the point j% along the line from a to tension point
//     		double aX = a.getX() + (j * (distanceAX/100));
//     		double aY = a.getY() + (j * (distanceAY/100));
//     		   
//     		//calculates the point j% along the line from tension point to b
//     		double bX = a.getTensionX() + (j * (distanceBX/100));
//     		double bY = a.getTensionY() + (j * (distanceBY/100));
//     	  
//     		//calculates the distance for the inner line
//     		double distanceCX = bX - aX; 
//     		double distanceCY = bY - aY; 
//     	   
//     		//calculates the final point on the inner line
//     		double fX = aX + (j * (distanceCX/100));
//     		double fY = aY + (j * (distanceCY/100));
//     		   
//     		bezierPoints.add(new Vector3d(fX, fY, 0));
//     	}   
//    	return bezierPoints;
//    }
    
    public static List<Vector3d> genCurve(Vector3d a, Vector3d b) {
    	
    	List<Vector3d> bezierPoints = new Vector<Vector3d>();
    	
    	//calculates the distance vector to the first tension point 		   
 		double distanceAX = a.getTensionX() - a.getX();
 		double distanceAY = a.getTensionY() - a.getY();
 		
 		//calculates the distance vector to the second tension from the first tension point
 		double distanceBX = a.getTensionX2() - a.getTensionX();
 		double distanceBY = a.getTensionY2() - a.getTensionY();
 		
 		//calculates the distance vector to the second control point from the second tension
 		double distanceCX = b.getX() - a.getTensionX2();
 		double distanceCY = b.getY() - a.getTensionY2();
 		   
     	for(int j = 0; j < 99; j++) {
     	   
     		//calculates the point j% along the line from the first control point to the first tension point
     		double aX = a.getX() + (j * (distanceAX/100));
     		double aY = a.getY() + (j * (distanceAY/100));
     		   
     		//calculates the point j% along the line from the first tension point to the second tension point
     		double bX = a.getTensionX() + (j * (distanceBX/100));
     		double bY = a.getTensionY() + (j * (distanceBY/100));
     		
     		//calculates the point j% along the line from the second tension point to the second control point
     		double cX = a.getTensionX2() + (j * (distanceCX/100));
     		double cY = a.getTensionY2() + (j * (distanceCY/100));
     	  
     		//calculates the distance for the first inner line
     		double distancePX = bX - aX; 
     		double distancePY = bY - aY; 
     		
     		//calculates the distance for the second inner line
     		double distanceQX = cX - bX;
     		double distanceQY = cY - bY;
     	   
     		//calculates the point j% along the first inner line
     		double pX = aX + (j * (distancePX/100));
     		double pY = aY + (j * (distancePY/100));
     		
     		//calculates the point j% along the second inner line
     		double qX = bX + (j * (distanceQX/100));
     		double qY = bY + (j * (distanceQY/100));

     		//calculates the distance for the last inner line
     		double distanceFX = qX - pX;
     		double distanceFY = qY - pY;
     		
     		//calculates the final point j% on the inner line
     		double fX = pX + (j * (distanceFX/100));
     		double fY = pY + (j * (distanceFY/100));
     		
     		bezierPoints.add(new Vector3d(fX, fY, 0));
     	}   
    	return bezierPoints;
    }
}

