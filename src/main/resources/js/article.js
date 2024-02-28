// 삭제 기능
const deleteButton = document.getElementById('delete-btn');

if (deleteButton) {
    deleteButton.addEventListener('click', event => {
        let id = document.getElementById('article-id').value;

//        fetch(`/api/articles/${id}`, {
//            method: 'DELETE'
//        })
//        .then(() => {
//            alert("삭제가 완료되었습니다.");
//            location.replace('/articles');
//        });

        function success() {
            alert("삭제가 완료되었습니다.");
            location.replace("/articles");
        }

        function fail() {
            alert("삭제에 실패했습니다.")
            location.replace("/articles");
        }

        httpRequest("DELETE", "/api/articles/" + id, null, success, fail);
    })
}

// 수정 기능
// id가 modify-btn인 엘리멘트 조회
const modifyButton = document.getElementById('modify-btn');

if(modifyButton) {
    modifyButton.addEventListener('click', event => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id');

//        fetch(`/api/articles/${id}`, {
//            method : 'PUT' ,
//            headers : {
//                "Content-Type" : "application/json",
//            },
//            body : JSON.stringify({
//                title : document.getElementById('title').value,
//                content : document.getElementById('content').value
//            })
//        })
//        .then(() => {
//            alert('수정이 완료되었습니다.');
//            location.replace(`/articles/${id}`);
//        });

        body = JSON.stringify({
            title : document.getElementById("title").value,
            content : document.getElementById("content").value,
        });

        function success() {
            alert("수정 완료되었습니다.");
            location.replace("/articles/" + id);
        }

        function fail() {
            alert("수정 실패했습니다.");
            location.replace("/articles/" + id);
        }

        httpRequest("PUT", "/api/articles/" + id, body, success, fail);
    });
}

// 등록 기능
// id가 create-btn인 엘리먼트
const createButton = document.getElementById('create-btn');

if(createButton) {
//
//        fetch("/api/articles", {
//            method : "POST",
//            headers : {
//                "Content-Type" : "application/json",
//            },
//            body: JSON.stringify({
//                title : document.getElementById("title").value,
//                content : document.getElementById("content").value,
//            }),
//        }).then(() => {
//            alert("등록 완료되었습니다.");
//            location.replace("/articles");
//        });

    // 등록 버튼을 클릭하면 /api/articles로 요청을 보냄
    createButton.addEventListener('click', (event) => {
        body = JSON.stringify({
            title : document.getElementById("title").value,
            content : document.getElementById("content").value,
        });
        function success() {
            alert("등록 완료되었습니다.");
            location.replace("/articles");
        }
        function fail() {
            alert("등록 실패했습니다.");
            location.replace("/articles");
        }

        httpRequest("POST", "/api/articles", body, success, fail);
    });
}

// 쿠키를 가져오는 함수
function getCookie(key) {
    var result = null;
    var cookie = document.cookie.split(";");
    cookie.some(function (item){
        item = item.replace(" ", "");

        var dic = item.split("=");

        if(key == dic[0]) {
            result = dic[1];
            return true;
        }
    });

    return result;
}

// HTTP 요청을 보내는 함수
// (1. 액세스 토큰도 함께 보내기)
// (2. 응답에서 권한 없다는 에러 발생시 -> 리프레시 토큰으로 새로운 액세스 토큰 요청)
// (3. 새로운 액세스 토큰으로 다시 요청 보냄)
function httpRequest(method, url, body, success, fail) {
    fetch(url, {
        method : method,
        headers : {
            // 로컬 스토리지에서 액세스 토큰 값을 가져와 헤더에 추가
            Authorization : "Bearer " + localStorage.getItem("access_token"),
            "Content-Type" : "application/json",
        },
        body : body,
    }).then((response) => {
        if (response.status == 200 || response.status == 201) {     // 200 : ok / 201 : created
            return success();
        }
        const refresh_token = getCookie("refresh_token");
        if(response.status === 401 && refresh_token) {      // 401 : unauthorized
            fetch("/api/token", {
                method: "POST",
                headers: {
                    Authorization : "Bearer " + localStorage.getItem("access_token"),
                    "Content-Type" : "application/json",

                },
                body : JSON.stringify({
                    refreshToken: getCookie("refresh_token"),
                }),
            })
                .then((res) => {
                    if(res.ok) {
                        return res.json();
                    }
                })

                .then((result) => {
                    // 재발급이 성공하면 로컬 스토리지값을 새로운 액세스 토큰으로 교체
                    localStorage.setItem("access_token", result.accessToken);
                    httpRequest(method, url, body, success, fail);  // 요청을 다시 보냄
                })

                .catch((error) => fail());

        } else {
            return fail();
        }
    });

}