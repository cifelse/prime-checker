import numpy as np
from scipy import stats

def t_test(array1, array2):
    # Calculate t-statistic and p-value
    t_statistic, p_value = stats.ttest_ind(array1, array2)
    
    return t_statistic, p_value

# Example usage
array1 = np.array([163059.6, 91571.0, 60568.4, 50679.6, 40688.8, 36682.8,37029.0, 38868.2,35673.2, 38854.4,36244.4]) #With Workers
array2 = np.array([259672.4, 166515.8, 93446.2,63012.8, 54851.0,53030.2,52481.6,51105.6,51983.8,52957.6,53595.6]) #Without workers

t_stat, p_value = t_test(array1, array2)
print("Null Hypothesis (H0): The means of the two samples are equal.")
print("Alternative Hypothesis (H1): The means of the two samples are not equal.")
print("T-statistic:", t_stat)
print("P-value:", p_value)

# Define significance level
alpha = 0.05

# Check if p-value is less than alpha
if p_value < alpha:
    print("Result: Reject the null hypothesis. There is enough evidence to support the alternative hypothesis.")
else:
    print("Result: Fail to reject the null hypothesis. There is not enough evidence to support the alternative hypothesis.")
