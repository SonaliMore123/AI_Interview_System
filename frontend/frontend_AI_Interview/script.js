let interviewId = null;

let questions = [];

let currentIndex = 0;

let selectedQuestion = "";



// ===============================
// START INTERVIEW
// ===============================

async function startInterview() {


    let role = document.getElementById("role").value;


    if(role.trim() === "") {

        alert("Please enter job role");

        return;
    }


    try {


        let response = await fetch(
            "http://localhost:8081/api/interview/start",
            {

                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({

                    role: role

                })

            }
        );


        let data = await response.json();


        console.log("Response:", data);



        interviewId = data.interviewId;



        questions = data.questions
            .split("\n")
            .filter(q => q.trim() !== "");



        console.log("Questions:", questions);



        showQuestions();



    }
    catch(error) {

        console.log(error);

        alert("Unable to start interview");

    }


}



// ===============================
// DISPLAY QUESTIONS
// ===============================

function showQuestions() {


    let html = "";


    questions.forEach((question,index)=>{


        html += `

        <button onclick="selectQuestion(${index})">

            ${question}

        </button>

        <br><br>

        `;


    });



    document.getElementById("questionList").innerHTML = html;



    document.getElementById("questionSection")
        .style.display = "block";


}





// ===============================
// SELECT QUESTION
// ===============================

function selectQuestion(index) {


    currentIndex = index;


    selectedQuestion = questions[index];



    document.getElementById("selectedQuestion")
        .innerHTML = selectedQuestion;



    document.getElementById("answerSection")
        .style.display = "block";



    document.getElementById("answer")
        .value = "";


}






// ===============================
// SUBMIT ANSWER
// ===============================

async function submitAnswer() {


    let answer =
        document.getElementById("answer").value;



    if(answer.trim() === "") {

        alert("Please enter answer");

        return;
    }



    try {


        let response = await fetch(
            "http://localhost:8081/api/interview/evaluate",
            {

                method:"POST",

                headers:{
                    "Content-Type":"application/json"
                },


                body:JSON.stringify({

                    interviewId: interviewId,

                    question: selectedQuestion,

                    answer: answer

                })

            }
        );



        let result = await response.json();



        console.log("Evaluation:", result);



        document.getElementById("feedbackSection")
            .style.display="block";



        document.getElementById("score")
            .innerHTML =
            "Score : " + result.score;



        document.getElementById("feedback")
            .innerHTML =
            "Feedback : " + result.feedback;



    }
    catch(error){

        console.log(error);

        alert("Evaluation failed");

    }


}






// ===============================
// NEXT QUESTION
// ===============================

function nextQuestion() {



    if(currentIndex < questions.length - 1) {


        currentIndex++;


        selectQuestion(currentIndex);



        document.getElementById("feedbackSection")
            .style.display="none";


    }
    else {


        showFinalResult();

    }


}







// ===============================
// FINAL RESULT
// ===============================

async function showFinalResult() {


    try {


        let response = await fetch(

            `http://localhost:8081/api/interview/result/${interviewId}`

        );



        let result = await response.json();



        console.log("Final Result:", result);



        document.getElementById("resultSection")
            .style.display="block";



        document.getElementById("finalScore")
            .innerHTML =

            `
            Total Score : ${result.totalScore}
            <br>
            Average Score : ${result.averageScore}
            <br>
            Answered Questions : ${result.answeredQuestions}
            `;



        document.getElementById("overallFeedback")
            .innerHTML =

            "Overall Feedback : "
            + result.overallFeedback;


    }
    catch(error){

        console.log(error);

        alert("Result loading failed");

    }


}