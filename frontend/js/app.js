const API_BASE_URL = "http://localhost:8080/api";

const state = {
    token: localStorage.getItem("ai_research_agent_token"),
    theme: localStorage.getItem("ai_research_agent_theme") || "dark"
};

document.addEventListener("DOMContentLoaded", () => {
    applyTheme();
    bindAuthForms();
    bindDashboard();
    bindResearch();
    bindChat();
    bindUpload();
    bindHistory();
    bindSharedActions();
});

function applyTheme() {
    if (state.theme === "light") {
        document.body.classList.add("light-theme");
    }
}

function bindSharedActions() {
    const toggle = document.getElementById("themeToggle");
    if (toggle) {
        toggle.addEventListener("click", () => {
            document.body.classList.toggle("light-theme");
            state.theme = document.body.classList.contains("light-theme") ? "light" : "dark";
            localStorage.setItem("ai_research_agent_theme", state.theme);
        });
    }

    const logoutBtn = document.getElementById("logoutBtn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", () => {
            localStorage.removeItem("ai_research_agent_token");
            localStorage.removeItem("ai_research_agent_user");
            window.location.href = "login.html";
        });
    }
}

function bindAuthForms() {
    const loginForm = document.getElementById("loginForm");
    const signupForm = document.getElementById("signupForm");

    if (loginForm) {
        loginForm.addEventListener("submit", async (event) => {
            event.preventDefault();
            await handleAuth("/auth/login", {
                email: document.getElementById("loginEmail").value,
                password: document.getElementById("loginPassword").value
            });
        });
    }

    if (signupForm) {
        signupForm.addEventListener("submit", async (event) => {
            event.preventDefault();
            await handleAuth("/auth/signup", {
                fullName: document.getElementById("signupName").value,
                email: document.getElementById("signupEmail").value,
                password: document.getElementById("signupPassword").value
            });
        });
    }
}

async function handleAuth(endpoint, payload) {
    const message = document.getElementById("authMessage");
    message.textContent = "Connecting...";
    try {
        const data = await apiFetch(endpoint, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        }, false);
        localStorage.setItem("ai_research_agent_token", data.token);
        localStorage.setItem("ai_research_agent_user", data.fullName);
        window.location.href = "dashboard.html";
    } catch (error) {
        message.textContent = error.message;
    }
}

async function bindDashboard() {
    if (document.body.dataset.page !== "dashboard") {
        return;
    }
    guardProtectedPage();
    const data = await apiFetch("/dashboard");
    document.getElementById("researchCount").textContent = data.totalResearchCount;
    document.getElementById("documentCount").textContent = data.totalUploadedDocuments;
    document.getElementById("chatCount").textContent = data.totalChatMessages;
    renderList("recentTopics", data.recentTopics);
    renderList("uploadedDocuments", data.uploadedDocuments);
    document.getElementById("activitySnapshot").textContent = data.activitySnapshot;
}

function bindResearch() {
    if (document.body.dataset.page !== "research") {
        return;
    }
    guardProtectedPage();
    const form = document.getElementById("researchForm");
    const citationForm = document.getElementById("citationForm");
    let lastSummary = "";
    setupVoiceSearch();

    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        document.getElementById("researchLoading").classList.remove("d-none");
        try {
            const response = await apiFetch("/research", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ topic: document.getElementById("researchTopic").value })
            });
            lastSummary = response.summary;
            document.getElementById("researchResult").classList.remove("d-none");
            document.getElementById("resultTopic").textContent = response.topic;
            document.getElementById("resultSummary").textContent = response.summary;
            renderList("resultInsights", response.keyInsights);
            renderList("resultPoints", response.importantPoints);
            renderList("resultRelated", response.relatedTopics);
            renderRecommendations(response.topic, response.relatedTopics);
        } catch (error) {
            alert(error.message);
        } finally {
            document.getElementById("researchLoading").classList.add("d-none");
        }
    });

    citationForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        const response = await apiFetch("/citations", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                title: document.getElementById("citationTitle").value,
                authors: document.getElementById("citationAuthors").value,
                year: document.getElementById("citationYear").value,
                publisher: document.getElementById("citationPublisher").value,
                url: document.getElementById("citationUrl").value,
                style: document.getElementById("citationStyle").value
            })
        });
        const result = document.getElementById("citationResult");
        result.classList.remove("d-none");
        result.textContent = response.generatedCitation;
    });

    document.getElementById("exportSummaryBtn").addEventListener("click", async () => {
        await exportSummary(lastSummary, "txt", "research-summary.txt");
    });

    document.getElementById("exportPdfBtn").addEventListener("click", async () => {
        await exportSummary(lastSummary, "pdf", "research-summary.pdf");
    });
}

function setupVoiceSearch() {
    const button = document.getElementById("voiceSearchBtn");
    const input = document.getElementById("researchTopic");
    if (!button || !input) {
        return;
    }

    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SpeechRecognition) {
        button.disabled = true;
        button.textContent = "No Mic";
        return;
    }

    const recognition = new SpeechRecognition();
    recognition.lang = "en-US";
    recognition.onresult = (event) => {
        input.value = event.results[0][0].transcript;
    };

    button.addEventListener("click", () => recognition.start());
}

async function bindChat() {
    if (document.body.dataset.page !== "chat") {
        return;
    }
    guardProtectedPage();
    const chatBox = document.getElementById("chatMessages");
    const history = await apiFetch("/chat/history");
    history.forEach(item => renderChatBubble(chatBox, item.role, item.message));

    document.getElementById("chatForm").addEventListener("submit", async (event) => {
        event.preventDefault();
        const input = document.getElementById("chatInput");
        const submitButton = event.currentTarget.querySelector("button[type='submit']");
        const message = input.value.trim();
        if (!message) {
            return;
        }
        renderChatBubble(chatBox, "user", message);
        input.value = "";
        const typingBubble = renderChatBubble(chatBox, "assistant", "Typing...");
        input.disabled = true;
        if (submitButton) {
            submitButton.disabled = true;
        }

        try {
            const response = await apiFetch("/chat", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ message })
            });
            typingBubble.textContent = "";
            typeText(typingBubble, response.reply.message);
        } catch (error) {
            typingBubble.textContent = `Sorry, I couldn't respond: ${error.message}`;
        } finally {
            input.disabled = false;
            if (submitButton) {
                submitButton.disabled = false;
            }
            input.focus();
        }
    });
}

function bindUpload() {
    if (document.body.dataset.page !== "upload") {
        return;
    }
    guardProtectedPage();
    document.getElementById("uploadForm").addEventListener("submit", async (event) => {
        event.preventDefault();
        const file = document.getElementById("pdfFile").files[0];
        const formData = new FormData();
        formData.append("file", file);

        const response = await fetch(`${API_BASE_URL}/files/upload`, {
            method: "POST",
            headers: { "Authorization": `Bearer ${state.token}` },
            body: formData
        });
        const data = await response.json();
        if (!response.ok) {
            alert(data.error || "Upload failed.");
            return;
        }
        document.getElementById("uploadResult").classList.remove("d-none");
        document.getElementById("uploadFileName").textContent = data.fileName;
        document.getElementById("uploadSummary").textContent = data.summary;
        document.getElementById("uploadPreview").textContent = data.extractedTextPreview;
    });
}

async function bindHistory() {
    if (document.body.dataset.page !== "history") {
        return;
    }
    guardProtectedPage();
    const items = await apiFetch("/research/history");
    const container = document.getElementById("historyList");
    container.innerHTML = "";
    items.forEach(item => {
        const card = document.createElement("article");
        card.className = "history-card";
        card.innerHTML = `<h3>${item.topic}</h3><p>${item.summary}</p><small>${new Date(item.createdAt).toLocaleString()}</small>`;
        container.appendChild(card);
    });
}

function renderList(elementId, items = []) {
    const element = document.getElementById(elementId);
    if (!element) {
        return;
    }
    element.innerHTML = "";
    items.forEach(item => {
        const li = document.createElement("li");
        li.textContent = item;
        element.appendChild(li);
    });
}

function renderChatBubble(container, role, message) {
    const bubble = document.createElement("div");
    bubble.className = `chat-bubble ${role}`;
    bubble.textContent = message;
    container.appendChild(bubble);
    container.scrollTop = container.scrollHeight;
    return bubble;
}

function renderRecommendations(topic, relatedTopics = []) {
    const target = document.getElementById("recommendationList");
    if (!target) {
        return;
    }
    const suggestions = [
        `Compare "${topic}" with one adjacent domain for broader context.`,
        `Turn one related topic into a focused literature review question.`,
        `Use the chatbot to ask for methods, limitations, and real-world applications.`,
        ...relatedTopics.slice(0, 2).map(item => `Create a follow-up search for "${item}".`)
    ];
    target.innerHTML = suggestions.map(item => `<p class="mb-2">${item}</p>`).join("");
}

function typeText(element, text) {
    let index = 0;
    const timer = setInterval(() => {
        element.textContent += text[index] || "";
        index += 1;
        if (index >= text.length) {
            clearInterval(timer);
        }
    }, 12);
}

function guardProtectedPage() {
    if (!state.token) {
        window.location.href = "login.html";
        throw new Error("Authentication required.");
    }
}

async function apiFetch(endpoint, options = {}, auth = true) {
    const headers = options.headers || {};
    if (auth && state.token) {
        headers.Authorization = `Bearer ${state.token}`;
    }
    const response = await fetch(`${API_BASE_URL}${endpoint}`, { ...options, headers });
    const contentType = response.headers.get("content-type") || "";
    const data = contentType.includes("application/json")
        ? await response.json()
        : { error: await response.text() };
    if (!response.ok) {
        throw new Error(data.error || "Request failed.");
    }
    return data;
}

async function exportSummary(content, format, fileName) {
    if (!content) {
        return;
    }
    const response = await fetch(`${API_BASE_URL}/export/${format}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${state.token}`
        },
        body: JSON.stringify({ content })
    });
    const blob = await response.blob();
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement("a");
    anchor.href = url;
    anchor.download = fileName;
    anchor.click();
    URL.revokeObjectURL(url);
}
