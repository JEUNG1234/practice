function init(){
    getBoardList(drawBoardList);
}

function getUrlPrams(id){
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(id);
}

function initBoard(){
    const boardId = getUrlPrams('board_id');

    getBoard(boardId, function(board){
        document.querySelector('#title').value = board.title;
        document.querySelector('#memberEmail').value = board.member_email;
        document.querySelector('#contents').value = board.contents;
        document.querySelector('#originFile').value = board.file_name;
        document.querySelector('#fileName').innerHTML = board.file_name;
        document.querySelector('#fileName').href = board.file_name;
    })
}

function formatDate(date) {
    let now = new Date(date);
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

function getBoard(boardId, callback){
    $.ajax({
        url: "http://localhost:8888/board/" + boardId,
        type: "get",                            
        contentType: "application/json",       
        success: function(response) {
            console.log(response)
            callback(response)
        },
        error: function() {
            console.error("board Request failed");
        }
    });
}

function getBoardList(callback){
    $.ajax({
        url: "http://localhost:8888/board",
        type: "get",                            
        contentType: "application/json",       
        success: function(response) {
            callback(response)
        },
        error: function() {
            console.error("board Request failed");
        }
    });
}

function drawBoardList(boardList){
    const boardBody = document.querySelector("#board-table tbody");

    if (boardList && boardList.length > 0) {
        boardBody.innerHTML = "";
        for(let board of boardList){
            boardBody.innerHTML += `<tr onclick="location.href='/boardDetail.html?board_id=${board.board_id}'">
                                        <td>${board.board_id}</td>
                                        <td>${board.title}</td>
                                        <td>${board.member_email}</td>
                                        <td>${formatDate(board.created_at)}</td>
                                    </tr>`
        }
    } else {
        boardBody.innerHTML = ` <tr>
                                    <td colspan="3">게시글이 없습니다</div></td>
                                </tr>`;
    }
}

function insertBoard(){
    // 입력값 가져오기

    const formData = new FormData();
    formData.append("title", document.querySelector('#title').value)
    formData.append("user_id", document.querySelector('#userId').value)
    formData.append("contents", document.querySelector('#contents').value)
    formData.append("upfile", document.querySelector('#upfile').files[0])


    // AJAX 요청 보내기
    $.ajax({
        url: "http://localhost:8888/board", // 서버의 글쓰기 API URL
        type: "POST",
        data: formData,
        contentType: false, // FormData를 사용하면 contentType은 false로 설정
        processData: false, // FormData를 사용할 경우 데이터 직렬화 비활성화
        success: function(response) {
            alert("글이 성공적으로 등록되었습니다.");
            window.location.href = "/"; // 성공 후 목록 페이지로 이동
        },
        error: function(error) {
            alert("글 등록에 실패했습니다.");
            console.error("board insert failed");
        }
    });
};

function updateBoard(){
    if(!confirm("글을 정말 수정하시겠습니까?"))
        return;

    const boardId = getUrlPrams('board_id');

    const formData = new FormData();
    formData.append("title", document.querySelector('#title').value)
    formData.append("contents", document.querySelector('#contents').value)
    formData.append("upfile", document.querySelector('#upfile').files[0])
    formData.append("origin_file", document.querySelector('#originFile').value)
    formData.append("boardId", boardId);

    $.ajax({
        url: "http://localhost:8888/board",
        type: "PUT",
        data: formData,
        contentType: false, // FormData를 사용하면 contentType은 false로 설정
        processData: false, // FormData를 사용할 경우 데이터 직렬화 비활성화
        success: function(response) {
            console.log(response)
            alert("글이 성공적으로 수정되었습니다.");
            window.location.href = "/boardDetail.html?board_id=" + boardId;
        },
        error: function(error) {
            alert("글 등록에 실패했습니다.");
            console.error("board insert failed");
        }
    });
};

function deleteBoard(){
    if(!confirm("글을 정말 삭제하시겠습니까?"))
        return;

    const boardId = getUrlPrams('board_id');

    $.ajax({
        url: "http://localhost:8888/board/" + boardId,
        type: "DELETE",
        success: function(response) {
            alert("글이 성공적으로 삭제되었습니다.");
            window.location.href = "/"; 
        },
        error: function(error) {
            alert("글 등록에 실패했습니다.");
            console.error("board insert failed");
        }
    });
};