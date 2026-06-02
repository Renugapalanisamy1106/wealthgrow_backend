package com.bfsi.dto;

import com.bfsi.entity.DataEvaluation;
import com.bfsi.entity.ScenarioAnalysis;
import com.bfsi.entity.ScenarioImpactResult;
import com.bfsi.entity.ScenarioNavSeries;

import java.util.List;

/**
 * ✅ NEW DTO: carries complete evaluation review data to Admin's sreview page.
 * Bundles: evaluation record, scenario metadata, impact results from BA, nav series for graph.
 */
public class EvaluationReviewDTO {

    private DataEvaluation evaluation;
    private ScenarioAnalysis scenario;
    private List<ScenarioImpactResult> impactResults;
    private List<ScenarioNavSeries> navSeries;

    public EvaluationReviewDTO() {}

    public EvaluationReviewDTO(DataEvaluation evaluation,
                                ScenarioAnalysis scenario,
                                List<ScenarioImpactResult> impactResults,
                                List<ScenarioNavSeries> navSeries) {
        this.evaluation    = evaluation;
        this.scenario      = scenario;
        this.impactResults = impactResults;
        this.navSeries     = navSeries;
    }

    public DataEvaluation getEvaluation()                { return evaluation; }
    public void setEvaluation(DataEvaluation v)          { this.evaluation = v; }

    public ScenarioAnalysis getScenario()                { return scenario; }
    public void setScenario(ScenarioAnalysis v)          { this.scenario = v; }

    public List<ScenarioImpactResult> getImpactResults() { return impactResults; }
    public void setImpactResults(List<ScenarioImpactResult> v) { this.impactResults = v; }

    public List<ScenarioNavSeries> getNavSeries()        { return navSeries; }
    public void setNavSeries(List<ScenarioNavSeries> v)  { this.navSeries = v; }
}
