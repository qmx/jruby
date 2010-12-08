package org.jruby.compiler.ir.dataflow.analyses;

import org.jruby.compiler.ir.IRClosure;
import org.jruby.compiler.ir.dataflow.DataFlowConstants;
import org.jruby.compiler.ir.dataflow.DataFlowProblem;
import org.jruby.compiler.ir.dataflow.FlowGraphNode;
import org.jruby.compiler.ir.operands.Variable;
import org.jruby.compiler.ir.representations.BasicBlock;
import java.util.Set;

public class FrameLoadPlacementProblem extends DataFlowProblem
{
/* ----------- Public Interface ------------ */
    public FrameLoadPlacementProblem()
    { 
        super(DataFlowProblem.DF_Direction.BACKWARD);
        _initLoadsOnExit = new java.util.HashSet<Variable>();
        _defVars = new java.util.HashSet<Variable>();
        _usedVars = new java.util.HashSet<Variable>();
    }

    public String        getName() { return "Frame Loads Placement Analysis"; }
    public FlowGraphNode buildFlowGraphNode(BasicBlock bb) { return new FrameLoadPlacementNode(this, bb);  }
    public String        getDataFlowVarsForOutput() { return ""; }
    public void          initLoadsOnScopeExit(Set<Variable> loads) { _initLoadsOnExit = loads; }
    public Set<Variable> getLoadsOnScopeExit() { return _initLoadsOnExit; }
    public void          recordDefVar(Variable v) { _defVars.add(v); }
    public void          recordUsedVar(Variable v) { _usedVars.add(v); }

    public boolean scopeDefinesVariable(Variable v) { 
        if (_defVars.contains(v)) {
            return true;
        }
        else {
            for (IRClosure cl: getCFG().getScope().getClosures()) {
                FrameLoadPlacementProblem nestedProblem = (FrameLoadPlacementProblem)cl.getCFG().getDataFlowSolution(DataFlowConstants.FLP_NAME);
                if (nestedProblem.scopeDefinesVariable(v)) 
                    return true;
            }

            return false;
        }
    }

    public boolean scopeUsesVariable(Variable v) { 
        if (_usedVars.contains(v)) {
            return true;
        }
        else {
            for (IRClosure cl: getCFG().getScope().getClosures()) {
                FrameLoadPlacementProblem nestedProblem = (FrameLoadPlacementProblem)cl.getCFG().getDataFlowSolution(DataFlowConstants.FLP_NAME);
                if (nestedProblem.scopeUsesVariable(v)) 
                    return true;
            }

            return false;
        }
    }

    public void addLoads()
    {
        for (FlowGraphNode n: _fgNodes) {
            FrameLoadPlacementNode flpn = (FrameLoadPlacementNode)n;
            flpn.addLoads();
        }
    }

/* ----------- Private Interface ------------ */
    private Set<Variable> _initLoadsOnExit;
    private Set<Variable> _defVars;      // Variables defined in this scope
    private Set<Variable> _usedVars;     // Variables used in this scope
}
