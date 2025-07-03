document.addEventListener("DOMContentLoaded", () => {
  const diaryId = window.diaryId;

  const colorMap = {
    행복해: "#ff7675",
    즐거워: "#fab1a0",
    감사해: "#74b9ff",
    사랑해: "#fd79a8",
    뿌듯해: "#55efc4",
    그저그래: "#dfe6e9",
    화나: "#ffeaa7",
    힘들어: "#a29bfe",
  };

  const totalBar = document.getElementById("total-bar");
  const tooltip = document.getElementById("tooltip");

  const startDateInput = document.getElementById("startDate");
  const endDateInput = document.getElementById("endDate");
  const applyBtn = document.getElementById("applyDateFilter");

  const openDatePickerBtn = document.getElementById("openDatePickerBtn");
  const datePickerModal = document.getElementById("datePickerModal");
  const closeModalBtn = document.getElementById("closeModalBtn");

  let stackedUserChart = null;
  let userPieChart = null;

  document.getElementById("backToDiaryListBtn").addEventListener("click", () => {
    location.href = `/entry/list?diaryId=${diaryId}`;
  });

  function showTooltip(e, text) {
    tooltip.style.opacity = 1;
    tooltip.style.left = e.pageX + 10 + "px";
    tooltip.style.top = e.pageY + 10 + "px";
    tooltip.textContent = text;
  }

  function hideTooltip() {
    tooltip.style.opacity = 0;
  }

  function renderTotalBar(totalcounts) {
    totalBar.innerHTML = "";
    const total = totalcounts.reduce((sum, e) => sum + e.count, 0);

    totalcounts.forEach((stat) => {
      const percent = total === 0 ? 0 : (stat.count / total) * 100;
      const displayPercent = percent > 0 && percent < 1 ? 1 : percent.toFixed(1);

      const segment = document.createElement("div");
      segment.className = "segment";
      segment.style.width = `${percent > 0 ? displayPercent : 0}%`;
      segment.style.backgroundColor = colorMap[stat.emotion] || "#ccc";

      segment.addEventListener("mousemove", (e) =>
        showTooltip(e, `${stat.emotion} (${percent.toFixed(1)}%)`)
      );
      segment.addEventListener("mouseleave", hideTooltip);

      totalBar.appendChild(segment);
    });
  }

  function renderUserCharts(data) {
    const userNicknames = data.usercounts.map((u) => u.nickname);
    const emotions = Object.keys(colorMap);

    const datasets = emotions.map((emotion) => ({
      label: emotion,
      data: data.usercounts.map((user) => {
        const found = user.emotionCounts.find((e) => e.emotion === emotion);
        return found ? found.count : 0;
      }),
      backgroundColor: colorMap[emotion] || "#ccc",
      stack: "emotion",
    }));

    if (stackedUserChart) stackedUserChart.destroy();
    stackedUserChart = new Chart(document.getElementById("stackedUserChart"), {
      type: "bar",
      data: {
        labels: userNicknames,
        datasets: datasets,
      },
      options: {
        responsive: true,
        maintainAspectRatio: true,
        plugins: {
          tooltip: {
            callbacks: {
              label: (ctx) => `${ctx.dataset.label}: ${ctx.parsed.y}회`,
            },
          },
          legend: { position: "top" },
        },
        scales: {
          x: {
            stacked: true,
            ticks: {
              maxRotation: 0,
              minRotation: 0,
              autoSkip: true,
              maxTicksLimit: 20,
            },
          },
          y: {
            stacked: true,
            beginAtZero: true,
            ticks: { stepSize: 1 },
          },
        },
      },
    });

    const pieLabels = data.selectedUserCount.emotionCounts.map((e) => e.emotion);
    const pieCounts = data.selectedUserCount.emotionCounts.map((e) => e.count);
    const pieColors = pieLabels.map((e) => colorMap[e] || "#ccc");

    if (userPieChart) userPieChart.destroy();
    userPieChart = new Chart(document.getElementById("userPieChart"), {
      type: "doughnut",
      data: {
        labels: pieLabels,
        datasets: [
          {
            data: pieCounts,
            backgroundColor: pieColors,
          },
        ],
      },
      options: {
        plugins: {
          tooltip: {
            callbacks: {
              label: (ctx) => `${ctx.label}: ${ctx.parsed}회`,
            },
          },
          legend: { position: "bottom" },
        },
      },
    });
  }

  function fetchStats(start, end) {
    const params = new URLSearchParams();
    if (start) params.append("start", start);
    if (end) params.append("end", end);

    fetch(`/api/emotion/${diaryId}/stat?${params.toString()}`)
      .then((res) => res.json())
      .then((data) => {
        renderTotalBar(data.totalcounts);
        renderUserCharts(data);
      });
  }

  function toSeoulISOString(date) {
    const pad = (n) => n.toString().padStart(2, "0");
    const year = date.getFullYear();
    const month = pad(date.getMonth() + 1);
    const day = pad(date.getDate());
    const hours = pad(date.getHours());
    const minutes = pad(date.getMinutes());
    const seconds = pad(date.getSeconds());
    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
  }

  openDatePickerBtn.addEventListener("click", () => {
    datePickerModal.style.display = "block";
  });

  closeModalBtn.addEventListener("click", () => {
    datePickerModal.style.display = "none";
  });

  window.addEventListener("click", (event) => {
    if (event.target === datePickerModal) {
      datePickerModal.style.display = "none";
    }
  });

  applyBtn.addEventListener("click", () => {
    let start = startDateInput.value ? new Date(startDateInput.value) : null;
    let end = endDateInput.value ? new Date(endDateInput.value) : null;

    if (end) {
      end.setHours(23, 59, 59, 999);
    }

    fetchStats(
      start ? toSeoulISOString(start) : null,
      end ? toSeoulISOString(end) : null
    );
    datePickerModal.style.display = "none";
  });

  fetchStats();
});
