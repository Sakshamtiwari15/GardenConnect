/**
 * GardenConnect - Chart.js Visualizations
 */
function initAdminCharts(categoryLabels, categoryData, statusLabels, statusData) {
    // 1. Tips by Category Doughnut Chart
    const categoryCtx = document.getElementById('tipsByCategoryChart');
    if (categoryCtx && categoryLabels && categoryLabels.length > 0) {
        new Chart(categoryCtx, {
            type: 'doughnut',
            data: {
                labels: categoryLabels,
                datasets: [{
                    data: categoryData,
                    backgroundColor: [
                        '#245b3a', '#4d8061', '#72a084', '#99c0a7',
                        '#d8a657', '#e0b875', '#e9cb96', '#3d84a8'
                    ],
                    borderWidth: 2,
                    borderColor: '#ffffff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            boxWidth: 12,
                            font: { family: 'Inter', size: 12 }
                        }
                    }
                }
            }
        });
    }

    // 2. Projects by Status Bar Chart
    const statusCtx = document.getElementById('projectsByStatusChart');
    if (statusCtx && statusLabels && statusLabels.length > 0) {
        new Chart(statusCtx, {
            type: 'bar',
            data: {
                labels: statusLabels,
                datasets: [{
                    label: 'Projects',
                    data: statusData,
                    backgroundColor: ['#6c757d', '#245b3a', '#2d8a4e', '#d8a657'],
                    borderRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: { stepSize: 1 }
                    }
                },
                plugins: {
                    legend: { display: false }
                }
            }
        });
    }
}
