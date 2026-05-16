(() => {
  const storageKey = "ds-theme";
  const root = document.documentElement;
  const mediaQuery = window.matchMedia("(prefers-color-scheme: dark)");

  function resolveTheme() {
    const savedTheme = localStorage.getItem(storageKey);
    if (savedTheme === "light" || savedTheme === "dark") {
      return savedTheme;
    }
    return mediaQuery.matches ? "dark" : "light";
  }

  function updateToggleLabel(theme) {
    document.querySelectorAll("[data-theme-toggle]").forEach((button) => {
      const nextTheme = theme === "dark" ? "light" : "dark";
      button.textContent = nextTheme === "dark" ? "Night mode" : "Light mode";
      button.setAttribute(
        "aria-label",
        nextTheme === "dark" ? "Ativar modo escuro" : "Ativar modo claro"
      );
    });
  }

  function applyTheme(theme, persist = false) {
    root.dataset.theme = theme;
    root.style.colorScheme = theme;
    if (persist) {
      localStorage.setItem(storageKey, theme);
    }
    updateToggleLabel(theme);
  }

  document.addEventListener("DOMContentLoaded", () => {
    applyTheme(resolveTheme());

    document.querySelectorAll("[data-theme-toggle]").forEach((button) => {
      button.addEventListener("click", () => {
        const nextTheme = root.dataset.theme === "dark" ? "light" : "dark";
        applyTheme(nextTheme, true);
      });
    });
  });

  mediaQuery.addEventListener("change", (event) => {
    const savedTheme = localStorage.getItem(storageKey);
    if (savedTheme !== "light" && savedTheme !== "dark") {
      applyTheme(event.matches ? "dark" : "light");
    }
  });
})();
