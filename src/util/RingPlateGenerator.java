package util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.GeneralPath;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.TextElement;
import org.openscience.cdk.renderer.elements.path.Close;
import org.openscience.cdk.renderer.elements.path.LineTo;
import org.openscience.cdk.renderer.elements.path.MoveTo;
import org.openscience.cdk.renderer.elements.path.PathElement;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.IGeneratorParameter;
import org.openscience.cdk.renderer.generators.parameter.AbstractGeneratorParameter;

import planar.BlockEmbedding;

public class RingPlateGenerator implements IGenerator<IAtomContainer> {

    private BlockEmbedding embedding;    // annoying...
    
    public RingPlateGenerator(BlockEmbedding embedding) {
        this.embedding = embedding;
    }

    public static class ShouldDrawRingNumbers extends AbstractGeneratorParameter<Boolean> {
        /** {@inheritDoc}} */
        public Boolean getDefault() {
            return Boolean.TRUE;
        }
    }
    private ShouldDrawRingNumbers shouldDrawRingNumbers = new ShouldDrawRingNumbers();

    @Override
    public List<IGeneratorParameter<?>> getParameters() {
        return Arrays.asList(new IGeneratorParameter<?>[] {
                shouldDrawRingNumbers
        });
    }

    @Override
    public IRenderingElement generate(IAtomContainer atomContainer, RendererModel model) {
        ElementGroup ringPaths = new ElementGroup();
        SignatureRingColorer ringColorer = new SignatureRingColorer(atomContainer, embedding);
        
        // 're-discovering' the color indices in this way seems clunky
        int maxColorIndex = 0;
        Map<Color, Integer> colorIndices = new HashMap<Color, Integer>();
        
        for (IRing ring : ringColorer.getRings()) {
            Point2d center = GeometryTools.get2DCenter(ring);
            Color color = ringColorer.getColorForRing(ring);
            ringPaths.add(generateRing(ring, center, color));
            if (shouldDrawRingNumbers.getValue()) {
                int colorIndex;
                if (colorIndices.containsKey(color)) {
                    colorIndex = colorIndices.get(color);
                } else {
                    colorIndex = maxColorIndex;
                    colorIndices.put(color, colorIndex);
                    maxColorIndex++;
                }
                ringPaths.add(new TextElement(
                        center.x, center.y, String.valueOf(colorIndex), Color.BLACK));
            }
        }
        return ringPaths;
    }

    private GeneralPath generateRing(IAtomContainer atomContainer, Point2d center, Color color) {
        List<PathElement> pathElements = new ArrayList<PathElement>();
        for (int index = 0; index < atomContainer.getAtomCount(); index++) {
            IAtom atom = atomContainer.getAtom(index);
            Point2d p = atom.getPoint2d();
            Vector2d v = new Vector2d(center);
            v.sub(p);
            v.scaleAdd(0.2, v, p);
            Point2d np = new Point2d(v);
            if (index == 0) {
                pathElements.add(new MoveTo(np));
            } else {
                pathElements.add(new LineTo(np));
            }
        }
        pathElements.add(new Close());
        GeneralPath genPath = new GeneralPath(pathElements, color);
        return genPath;
    }
}
