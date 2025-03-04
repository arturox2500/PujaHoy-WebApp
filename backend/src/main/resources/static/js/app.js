const sleep = ms => new Promise(r => setTimeout(r, ms));
let page = 1;
let noMorePosts = false;

async function cargarPosts() {
    if (noMorePosts) {
        return;
    }
    console.log(page);
    
    document.getElementById("spinnerY").style.display = "block"; // Mostrar el spinner
    document.getElementById("spinnerY").style.visibility = "visible";
    document.getElementById("load-more-yposts").style.display = "none";
    await sleep(300);
    try {
        const response = await fetch(`/usuario/producto_template?pagina=${page}`);
        const responseNext = await fetch(`/usuario/producto_template?pagina=${page+1}`);
        if (response.ok) {
            const nuevosPostsHTML = (await response.text()); 
            if (nuevosPostsHTML != "") {
                
                document.getElementById("yourProdsRow").innerHTML += nuevosPostsHTML;
                document.getElementById("load-more-yposts").style.display = "inline";
                nextHTML = await responseNext.text()
                if (nextHTML.trim() == "" || !responseNext.ok) {
                    
                    
                    noMorePosts = true;
                    document.getElementById("load-more-yposts").style.display = "none";
                    document.getElementById("spinnerY").style.visibility = "hidden";
                }
            }
            if (!noMorePosts) {
                document.getElementById("load-more-yposts").style.display = "inline";
            }
            
            document.getElementById("spinnerY").style.visibility = "hidden";
            
            page++;
        }
    } catch (error) {
        console.error("Error al cargar los posts:", error);
    }

    document.getElementById("spinnerY").style.display = "none"; // Ocultar el spinner
    
}

document.addEventListener("DOMContentLoaded", function () {
    const btnY = document.getElementById("load-more-yposts");
    if (btnY) {
        btnY.addEventListener("click", cargarPosts);
    }
});


async function cargarPosts2() {
    
    if (noMorePosts) {
        return;
    }
    
    document.getElementById("spinnerWB").style.display = "block"; // Mostrar el spinner
    document.getElementById("spinnerWB").style.visibility = "visible";
    document.getElementById("load-more-WBposts").style.display = "none";
    await sleep(300);
    try {
        const response = await fetch(`/usuario/producto_template_compras?pagina=${page}`);
        const responseNext = await fetch(`/usuario/producto_template_compras?pagina=${page+1}`);
        if (response.ok) {
            const nuevosPostsHTML = await response.text(); 
            
            if (nuevosPostsHTML !== "") {
                document.getElementById("yourWBRow").innerHTML += nuevosPostsHTML;
                document.getElementById("load-more-WBposts").style.display = "inline";
                nextHTML = await responseNext.text()
                if (nextHTML.trim() == "" || !responseNext.ok) {                   
                    noMorePosts = true;
                    document.getElementById("load-more-WBposts").style.display = "none";
                    document.getElementById("spinnerWB").style.visibility = "hidden";
                }
            }
            
            if (!noMorePosts) {
                document.getElementById("load-more-WBposts").style.display = "inline";
            }
            
            document.getElementById("spinnerWB").style.visibility = "hidden";

            page++;
        }
    } catch (error) {
        console.error("Error al cargar los posts:", error);
    }

    document.getElementById("spinnerWB").style.display = "none"; // Ocultar el spinner
}

document.addEventListener("DOMContentLoaded", function () {
    const btnWB = document.getElementById("load-more-WBposts");
    if (btnWB) btnWB.addEventListener("click", cargarPosts2);
});

async function cargarIndex() {
    
    if (noMorePosts) {
        return;
    }
    
    document.getElementById("spinnerIndex").style.display = "block"; // Mostrar el spinner
    document.getElementById("spinnerIndex").style.visibility = "visible";
    document.getElementById("load-more-index").style.display = "none";
    await sleep(300);
    try {
        const response = await fetch(`/producto_template_index?page=${page}`);
        const responseNext = await fetch(`/producto_template_index?page=${page+1}`);
        if (response.ok) {
            const nuevosPostsHTML = await response.text(); 
            
            if (nuevosPostsHTML !== "") {
                document.getElementById("indexProduct").innerHTML += nuevosPostsHTML;
                document.getElementById("load-more-index").style.display = "inline";
                if (!responseNext.ok || (await responseNext.text()).trim() === "") {                   
                    noMorePosts = true;
                    document.getElementById("load-more-index").style.display = "none";
                    document.getElementById("spinnerIndex").style.display = "hidden";
                }
            }
            
            if (!noMorePosts) {
                document.getElementById("load-more-index").style.display = "inline";
            }
            
            document.getElementById("spinnerIndex").style.visibility = "hidden";

            page++;
        }
    } catch (error) {
        console.error("Error al cargar los posts:", error);
    }

    

    document.getElementById("spinnerIndex").style.display = "none"; // Ocultar el spinner
}

document.addEventListener("DOMContentLoaded", function () {
    const btnIndex = document.getElementById("load-more-index");
    if (btnIndex) btnIndex.addEventListener("click", cargarIndex);
});



