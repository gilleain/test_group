package projection;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class StereoProjector {
    
    public static void projectUpwards(IAtomContainer flat) {
        for (IAtom atom : flat.atoms()) {
            Point2d p = atom.getPoint2d();
            double xySq = (p.x * p.x) + (p.y * p.y);
            double x = (2 * p.x)  / (1 + xySq);
            double y = (2 * p.y)  / (1 + xySq);
            double z = (xySq - 1) / (1 + xySq);
            atom.setPoint3d(new Point3d(x, y, z));
            atom.setPoint2d(null);
        }
    }
    
    public static void projectDownwards(IAtomContainer curved) {
        for (IAtom atom : curved.atoms()) {
            Point3d p = atom.getPoint3d();
            double x = p.x / (1 - p.z);
            double y = p.y / (1 - p.z);
            atom.setPoint2d(new Point2d(x, y));
        }
    }

}
