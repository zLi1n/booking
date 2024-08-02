package com.example.booking.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.example.booking.answer.Answer;
import com.example.booking.choice_answer.ChoiceAnswer;
import com.example.booking.date_answer.DateAnswer;
import com.example.booking.display.Display;
import com.example.booking.display.DisplayRepository;
import com.example.booking.display.DisplayService;
import com.example.booking.number_answer.NumberAnswer;
import com.example.booking.question.Question;
import com.example.booking.question.QuestionService;
import com.example.booking.time_answer.TimeSlot;

import jakarta.transaction.Transactional;

@Service
public class FormService {
    @Autowired
    private FormRepository formRepository;
    @Autowired
    private DisplayRepository displayRepository;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private DisplayService displayService;

    public List<Form> getAllForms() {
        return formRepository.findAll();
    }

    public Form saveForm(Form form) {
        return formRepository.save(form);
    }

    public Optional<Form> getFormById(Long id) {
        return formRepository.findById(id);
    }

    public Form getFirstForm() {
        List<Form> forms = formRepository.findAll();
        return forms.isEmpty() ? null : forms.get(0);
    }

    public Map<String, Object> getFormStructure(Long formId) {
        Form form = getFormById(formId).orElseThrow(() -> new RuntimeException("Form not found"));
        return createTreeStructure(form.getFirstQuestionId());
    }

    private Map<String, Object> createTreeStructure(Long questionId) {
        Question question = questionService.getQuestionById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Map<String, Object> node = new HashMap<>();
        node.put("name", question.getText());
        node.put("type", question.getType());

        List<Map<String, Object>> children = new ArrayList<>();
        for (Answer answer : question.getAnswers()) {
            Map<String, Object> childNode = new HashMap<>();
            childNode.put("name", getAnswerText(answer));
            if (answer.getNextQuestion() != null) {
                childNode.put("children",
                        Collections.singletonList(createTreeStructure(answer.getNextQuestion().getqId())));
            }
            children.add(childNode);
        }
        if (!children.isEmpty()) {
            node.put("children", children);
        }
        return node;
    }

    private String getAnswerText(Answer answer) {
        int qty = answer.getQty() == null ? 0 : answer.getQty();
        String answerText;
        String lineSeparator = System.lineSeparator();

        if (answer instanceof ChoiceAnswer) {
            answerText = ((ChoiceAnswer) answer).getText();
        } else if (answer instanceof DateAnswer) {
            answerText = ((DateAnswer) answer).getDate().toString();
        } else if (answer instanceof NumberAnswer) {
            answerText = "Min: " + ((NumberAnswer) answer).getMinNum() + ", Max: "
                    + ((NumberAnswer) answer).getMaxNum();
        } else if (answer instanceof TimeSlot) {
            answerText = ((TimeSlot) answer).getFormattedTimeRange();
        } else {
            answerText = "Answer will be provided later.";
        }

        answerText += lineSeparator + "Quantity: " + qty;
        System.out.println("Answer Text: " + answerText);
        return answerText;
    }

    @Transactional
    public void deleteForm(Long id) {
        Form form = formRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Form not found"));
        Display display = displayService.getDisplayByFormId(id);
        try {
            if (form.getFirstQuestionId() != null) {
                questionService.deleteQuestion(form.getFirstQuestionId());
            }
            if (display != null) {
                displayRepository.delete(display);
            }
            formRepository.delete(form);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("This form cannot be deleted as it has associated bookings.");
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while deleting the form: " + e.getMessage(), e);
        }
    }

    public Form getFormByQuestionId(Long questionId) {
        Question question = questionService.getQuestionById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        while (question.getParent() != null) {
            question = question.getParent();
        }
        return formRepository.findByFirstQuestionId(question.getqId())
                .orElseThrow(() -> new RuntimeException("Form not found for the given question"));
    }
}
