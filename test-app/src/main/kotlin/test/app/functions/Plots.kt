package test.app.functions

import kscience.plotly.Plotly
import kscience.plotly.makeFile
//import kscience.plotly.models.Mode
//import kscience.plotly.models.Type
import kscience.plotly.trace
import kotlin.math.PI
import kotlin.math.sin

class Plots {
    fun plot2dExperiment(xlabel: String, xvalues: Int,
                         ylabel: String, yvalues: Int) {
        val xValues = (0..100).map { it.toDouble() / 100.0 }
        val yValues = xValues.map { sin(2.0 * PI * it) }
        val plot = Plotly.plot2D {
            Plotly.trace {

            }
        }
    }

}