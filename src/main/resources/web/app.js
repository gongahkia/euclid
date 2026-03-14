const templates = {
  formula: `x = pm(-b, sqrt(pow(b, 2) - 4 * a * c)) \\\\ (2 * a)`,
  "mixed-doc": `# Release notes

The stability region is defined by geq(r, 0) and the characteristic equation is:

integral(exp(-x), x, 0, INFINITY) = 1

We also track the bounded interval using:

leq(0, x) AND lt(x, 1)`,
  proof: `forall(n, exists(k, n = 2 * k))
NOT(p AND q) = NOT(p) OR NOT(q)
subset(A, B) = forall(x, implies(element_of(x, A), element_of(x, B)))`,
  "linear-algebra": `A = matrix([[1, 2], [3, 4]])
v = vector([x, y])
A * v = vector([x + 2 * y, 3 * x + 4 * y])`,
  stats: `mean = (sum(x_i, i, 1, n)) \\\\ n
variance = (sum(pow(x_i - mean, 2), i, 1, n)) \\\\ (n - 1)
prob(given(A, B)) = (prob(given(B, A)) * prob(A)) \\\\ prob(B)`
};

const sourceEditor = document.getElementById("sourceEditor");
const templateSelect = document.getElementById("templateSelect");
const mathModeSelect = document.getElementById("mathModeSelect");
const mixedModeToggle = document.getElementById("mixedModeToggle");
const transpileButton = document.getElementById("transpileButton");
const canonicalizeButton = document.getElementById("canonicalizeButton");
const copyOutputButton = document.getElementById("copyOutputButton");
const loadTemplateButton = document.getElementById("loadTemplateButton");
const capabilitySearch = document.getElementById("capabilitySearch");
const capabilityList = document.getElementById("capabilityList");
const capabilityCount = document.getElementById("capabilityCount");
const transpileOutput = document.getElementById("transpileOutput");
const canonicalOutput = document.getElementById("canonicalOutput");
const renderedPreview = document.getElementById("renderedPreview");
const diagnosticList = document.getElementById("diagnosticList");
const statusBadge = document.getElementById("statusBadge");

let capabilities = [];

function escapeHtml(value) {
  return value
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;");
}

function setStatus(text, variant = "idle") {
  statusBadge.textContent = text;
  statusBadge.dataset.variant = variant;
}

async function fetchJson(url, options = {}) {
  const response = await fetch(url, options);
  if (!response.ok) {
    throw new Error(`Request failed with status ${response.status}`);
  }
  return response.json();
}

function buildFormBody(source) {
  return new URLSearchParams({
    source,
    mathMode: mathModeSelect.value,
    mixedMode: String(mixedModeToggle.checked)
  });
}

function renderCapabilities(items) {
  capabilityCount.textContent = `${items.length} entries`;
  capabilityList.innerHTML = items.map((capability) => {
    const aliases = capability.aliases.map((alias) => `<span class="alias-badge">${escapeHtml(alias)}</span>`).join("");
    const signature = capability.signature
      ? `<div class="capability-signature">${escapeHtml(capability.signature.label)}</div>`
      : "";
    return `
      <article class="capability-card">
        <div class="panel-head">
          <h3>${escapeHtml(capability.name)}</h3>
          <span class="kind-badge ${capability.kind.toLowerCase()}">${escapeHtml(capability.kind.toLowerCase())}</span>
        </div>
        ${signature}
        ${aliases ? `<p>${aliases}</p>` : ""}
      </article>
    `;
  }).join("");
}

function filterCapabilities() {
  const query = capabilitySearch.value.trim().toLowerCase();
  if (!query) {
    renderCapabilities(capabilities);
    return;
  }
  const filtered = capabilities.filter((capability) => {
    return capability.name.toLowerCase().includes(query)
      || capability.kind.toLowerCase().includes(query)
      || capability.aliases.some((alias) => alias.toLowerCase().includes(query))
      || (capability.signature && capability.signature.label.toLowerCase().includes(query));
  });
  renderCapabilities(filtered);
}

function renderDiagnostics(diagnostics) {
  if (!diagnostics.length) {
    diagnosticList.innerHTML = `<article class="diagnostic-card info"><strong>No diagnostics.</strong><p>The current source transpiled without warnings or errors.</p></article>`;
    return;
  }

  diagnosticList.innerHTML = diagnostics.map((diagnostic) => {
    const suggestion = diagnostic.suggestion ? `<p><strong>Suggestion:</strong> ${escapeHtml(diagnostic.suggestion)}</p>` : "";
    const canonical = diagnostic.canonicalRewrite ? `<p><strong>Canonical form:</strong> <code>${escapeHtml(diagnostic.canonicalRewrite)}</code></p>` : "";
    const code = diagnostic.code ? `<span class="diagnostic-code">${escapeHtml(diagnostic.code)}</span>` : "";
    return `
      <article class="diagnostic-card ${diagnostic.severity.toLowerCase()}">
        <div>
          <strong>${escapeHtml(diagnostic.severity)}</strong>
          ${code}
        </div>
        <p>${escapeHtml(diagnostic.message)}</p>
        <p><strong>Location:</strong> line ${diagnostic.line}, column ${diagnostic.column}</p>
        ${suggestion}
        ${canonical}
      </article>
    `;
  }).join("");
}

function renderPreview(output) {
  if (!output) {
    renderedPreview.innerHTML = `<p class="subtle-text">No output yet.</p>`;
    return;
  }

  let mathMarkup = output;
  if (mathModeSelect.value === "NONE" && !mixedModeToggle.checked) {
    mathMarkup = `\\[${output}\\]`;
  }

  if (mixedModeToggle.checked) {
    renderedPreview.textContent = output;
  } else {
    renderedPreview.innerHTML = mathMarkup;
  }

  if (window.MathJax && typeof window.MathJax.typesetPromise === "function") {
    window.MathJax.typesetPromise([renderedPreview]).catch(() => {});
  }
}

async function transpileSource() {
  setStatus("Transpiling", "busy");
  try {
    const data = await fetchJson("/api/transpile", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8" },
      body: buildFormBody(sourceEditor.value)
    });

    canonicalOutput.textContent = data.canonicalSource || "";
    transpileOutput.textContent = data.output || "";
    renderDiagnostics(data.diagnostics || []);
    renderPreview(data.output || "");
    setStatus(data.hasErrors ? "Errors present" : "Up to date", data.hasErrors ? "error" : "ok");
  } catch (error) {
    renderDiagnostics([{ severity: "ERROR", message: error.message, line: 1, column: 1 }]);
    setStatus("Request failed", "error");
  }
}

async function canonicalizeSource() {
  setStatus("Canonicalizing", "busy");
  try {
    const data = await fetchJson("/api/canonicalize", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8" },
      body: new URLSearchParams({ source: sourceEditor.value })
    });
    sourceEditor.value = data.canonicalSource || sourceEditor.value;
    setStatus("Canonicalized", "ok");
    await transpileSource();
  } catch (error) {
    renderDiagnostics([{ severity: "ERROR", message: error.message, line: 1, column: 1 }]);
    setStatus("Canonicalization failed", "error");
  }
}

async function copyOutput() {
  try {
    await navigator.clipboard.writeText(transpileOutput.textContent);
    setStatus("Copied output", "ok");
  } catch {
    setStatus("Clipboard blocked", "warning");
  }
}

function loadTemplate() {
  sourceEditor.value = templates[templateSelect.value];
  transpileSource();
}

async function initialize() {
  setStatus("Loading", "busy");
  try {
    const data = await fetchJson("/api/capabilities");
    capabilities = data.capabilities || [];
    renderCapabilities(capabilities);
    loadTemplate();
  } catch (error) {
    renderDiagnostics([{ severity: "ERROR", message: error.message, line: 1, column: 1 }]);
    setStatus("Startup failed", "error");
  }
}

capabilitySearch.addEventListener("input", filterCapabilities);
transpileButton.addEventListener("click", transpileSource);
canonicalizeButton.addEventListener("click", canonicalizeSource);
copyOutputButton.addEventListener("click", copyOutput);
loadTemplateButton.addEventListener("click", loadTemplate);
mathModeSelect.addEventListener("change", transpileSource);
mixedModeToggle.addEventListener("change", transpileSource);
sourceEditor.addEventListener("keydown", (event) => {
  if ((event.metaKey || event.ctrlKey) && event.key === "Enter") {
    event.preventDefault();
    transpileSource();
  }
});

initialize();
