package com.inflearn.jpa;

import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootTest
class JpaApplicationTests {

	public static void main(String[] args) {
		Solution solution = new Solution();

		AbstractList<Integer> integers = new AbstractList<Integer>() {
			@Override
			public Integer get(int index) {
				return null;
			}

			@Override
			public int size() {
				return 0;
			}
		};
/*		int[] arr = {7,9,1,1,4};
		int result = solution.solution2(arr);
		System.out.println("@@ #1 RESULT :: " + result);*/

//		int[] q1 = new int[300000];
//		int[] q2 = new int[300000];
//
//		for(int i=0; i<300000; i++){
//			q1[i] = 1;
//			if(i== 300000-2)
//				q2[i] = 599999;
//			else q2[i] = 1;
//		}

		int[] q1 = {3, 2, 7, 2};
		int[] q2 = {4, 6, 5, 1};

		int result2 = solution.solution(q1, q2);
		System.out.println("@@ #2 RESULT :: " + result2);
	}

	static class Solution {

		int min = 600001;
		long goal;
		int size;
		int[] queue1, queue2;

		public int solution(int[] queue1, int[] queue2) {
			long sum1 = 0;
			long sum2 = 0;

			for(int i=0; i<queue1.length; i++){
				sum1 += parseLong(queue1[i]);
				sum2 += parseLong(queue2[i]);
			}

			if((sum1 + sum2) % 2 == 1)
				return -1;

			goal = (sum1 + sum2) / 2;
			size = queue1.length;
			this.queue1 = queue1;
			this.queue2 = queue2;
			removeAndAdd(0, 0, 0, sum1);

			if(min == 600001)
				min = -1;
			return min;
		}

		public long parseLong(int queue1) {
			return Long.parseLong(String.valueOf(queue1));
		}

		public void removeAndAdd(int i1, int i2, int cur, long sum) {
			while(true){
				if(cur >= min || i1>=size*2 || i2 >= size*2)
					break;
				if(sum == goal){
					min = cur;
					break;
				}

				int num1 = i1 >= size ? queue2[i1-size] : queue1[i1];
				int num2 = i2 >= size ? queue1[i2-size] : queue2[i2];

				if(goal < sum) {
					i1++;
					sum -= parseLong(num1);
				} else{
					i2++;
					sum += parseLong(num2);
				}
				cur ++;
			}

		}
		public int solution2(int[] elements) {
			Set<Integer> nums = new TreeSet<>();

			Arrays.stream(elements).forEach(nums::add);
			int size = elements.length;

			int l = 2;
			do {
				for (int i = 0; i < size; i++) {
					int sum = 0;
					for (int j = 0; j < l; j++) {
						int cur = (i + j) % size;
						sum += elements[cur];
					}
					nums.add(sum);
				}

			} while (++l <= size);


			return nums.size();
		}

	}
}
