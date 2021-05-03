package it.units.malelab.TreeNodes;

import it.units.malelab.BuildingBlocks.STLFormulaMapper;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import eu.quanticol.moonlight.signal.Signal;
import it.units.malelab.jgea.core.util.Sized;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;


public abstract class AbstractTreeNode implements Sized {

    protected Function<Signal<Map<String, Double>>, TemporalMonitor<Map<String, Double>, Double>> func;
    protected AbstractTreeNode firstChild;
    protected AbstractTreeNode secondChild;
    protected String symbol;
    protected final STLFormulaMapper mapper;

    public AbstractTreeNode(STLFormulaMapper map) {
        this.mapper = map;
    }

    public Function<Signal<Map<String, Double>>, TemporalMonitor<Map<String, Double>, Double>> getOperator() {
        return this.func;
    }

    public AbstractTreeNode getSecondChild() {
        return this.secondChild;
    }

    public AbstractTreeNode getFirstChild() {
        return this.firstChild;
    }

    public abstract int getNecessaryLength();

    public String getSymbol() {
        return this.symbol;
    }

    public List<String[]> getVariables() {
        List<String[]> ans  = new ArrayList<>();
        this.getVariablesAux(ans);
        return ans;
    }

    public abstract void getVariablesAux(List<String[]> temp);

    public abstract int getNumBounds();

    public void propagateParameters(double[] parameters) {
        this.propagateParametersAux(parameters, new int[] {0, this.getNumBounds()});
    }

    public abstract int[] propagateParametersAux(double[] parameters, int[] idxs);

    @Override
    public int size() {
        int ans = 1;
        ans += (this.firstChild != null) ? this.firstChild.size() : 0;
        ans += (this.secondChild != null) ? this.secondChild.size() : 0;
        return ans;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        else if (o.getClass() != this.getClass()) {
            return false;
        }
        final AbstractTreeNode other = (AbstractTreeNode) o;
        return Objects.equals(this.getSymbol(), other.getSymbol());
    }

    @Override
    public int hashCode() {
        int result = 7;
        result = 31 * result + (this.symbol == null ? 0 : this.symbol.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return traversePreOrder(this);
    }

    private static String traversePreOrder(AbstractTreeNode node) {
        if (node == null) {
            return "\n";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(node.getSymbol());
        String pointerRight = "└──";
        boolean hasRightChild = node.getFirstChild() != null;
        String pointerLeft = (hasRightChild) ? "├──" : "└──";
        traverseNodes(sb, "", pointerLeft, node.getSecondChild(), hasRightChild);
        traverseNodes(sb, "", pointerRight, node.getFirstChild(), false);
        sb.append("\n");
        return sb.toString();
    }

    private static void traverseNodes(StringBuilder sb, String padding, String pointer, AbstractTreeNode node, boolean hasRightSibling) {
        if (node != null) {
            sb.append("\n");
            sb.append(padding);
            sb.append(pointer);
            sb.append(node.getSymbol());
            StringBuilder paddingBuilder = new StringBuilder(padding);
            if (hasRightSibling) {
                paddingBuilder.append("│  ");
            }
            else {
                paddingBuilder.append("   ");
            }
            String paddingForBoth = paddingBuilder.toString();
            String pointerRight = "└──";
            boolean hasRightChild = node.getFirstChild() != null;
            String pointerLeft = (hasRightChild) ? "├──" : "└──";
            traverseNodes(sb, paddingForBoth, pointerLeft, node.getSecondChild(), hasRightChild);
            traverseNodes(sb, paddingForBoth, pointerRight, node.getFirstChild(), false);
        }
    }

}
