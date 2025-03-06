const sleep = ms => new Promise(r => setTimeout(r, ms));
let page = 1;
let noMorePosts = false;

async function cargarPosts() {
    if (noMorePosts) {
        return;
    }
    
    
    document.getElementById("spinnerY").style.display = "block"; // Mostrar el spinner
    document.getElementById("spinnerY").style.visibility = "visible";
    document.getElementById("load-more-yposts").style.display = "none";
    await sleep(300);
    try {
        const response = await fetch(`/user/product_template?page=${page}`);
        const responseNext = await fetch(`/user/product_template?page=${page+1}`);
        if (response.ok) {
            const nuevosPostsHTML = (await response.text()); 
            if (nuevosPostsHTML != "") {
                console.log(nuevosPostsHTML);
                
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
        const response = await fetch(`/user/product_template_buys?page=${page}`);
        const responseNext = await fetch(`/user/product_template_buys?page=${page+1}`);
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
        const response = await fetch(`/product_template_index?page=${page}`);
        const responseNext = await fetch(`/product_template_index?page=${page+1}`);
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

            updateStars()
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

document.addEventListener("DOMContentLoaded", function() {
    document.querySelectorAll(".product-rating").forEach(function(ratingElement) {
        const reputation = parseInt(ratingElement.getAttribute("data-reputation")) || 0;
        ratingElement.innerHTML = ""; // Limpiar contenido previo
        for (let i = 0; i < reputation; i++) {
            const star = document.createElement("i");
            star.className = "fa fa-star";
            ratingElement.appendChild(star);
        }
    });
});

function updateStars() {
    document.querySelectorAll(".product-rating").forEach(function(ratingElement) {
        if (ratingElement.hasAttribute("data-processed")) {
            return; // Evitar procesar un producto dos veces
        }

        const reputation = parseInt(ratingElement.getAttribute("data-reputation")) || 0;
        ratingElement.innerHTML = ""; // Limpiar contenido previo

        for (let i = 0; i < reputation; i++) {
            const star = document.createElement("i");
            star.className = "fa fa-star";
            ratingElement.appendChild(star);
        }

        ratingElement.setAttribute("data-processed", "true"); // Marcar como procesado
    });
}

// Ejecutar al cargar la página
document.addEventListener("DOMContentLoaded", actualizarEstrellas);



