package com.example.trivia_quizz_app.repositoryLayer

class ApiGraphRepository {
    private val QUICKCHART_BASE_URL = "https://quickchart.io/chart"
    fun getPieGraphUrl(correctCount: Int, wrongCount: Int): String {
        val pieGraphDefinition = """
        {
            type: "pie",
            data: {
                labels: ["correct answers", "wrong answers"],
                datasets: [{
                    data: [$correctCount, $wrongCount],
                    backgroundColor: ["%234CAF50", "%23F44336"],
                    borderColor: "black"
                }]
            },
            options: { 
              legend: {
                labels: {
                    fontColor: "white",
                    fontSize: 22
                }
            },
                plugins: {
                  datalabels: {
                    color: "black"
                  }
               }
            }
        }
        """.trimIndent()

        return "$QUICKCHART_BASE_URL?c=$pieGraphDefinition"
    }
}