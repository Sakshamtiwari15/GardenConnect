package com.gardenconnect.service;

import com.gardenconnect.concurrency.ActivityProcessor;
import com.gardenconnect.dao.DiscussionDAO;
import com.gardenconnect.dao.TipDAO;
import com.gardenconnect.exception.ResourceNotFoundException;
import com.gardenconnect.exception.ValidationException;
import com.gardenconnect.model.ModerationStatus;
import com.gardenconnect.model.Tip;
import com.gardenconnect.service.impl.ModerationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ModerationService Workflow Tests")
class ModerationServiceTest {

    @Mock
    private TipDAO tipDAO;

    @Mock
    private DiscussionDAO discussionDAO;

    @Mock
    private ActivityProcessor asyncActivityProcessor;

    private ModerationService moderationService;

    @BeforeEach
    void setUp() {
        moderationService = new ModerationServiceImpl(tipDAO, discussionDAO, asyncActivityProcessor);
    }

    @Test
    @DisplayName("Approve tip transitions status to APPROVED and logs activity")
    void testApproveTip() throws Exception {
        Tip pendingTip = new Tip();
        pendingTip.setId(100L);
        pendingTip.setTitle("Companion Planting with Basil");
        pendingTip.setModerationStatus(ModerationStatus.PENDING);

        when(tipDAO.findById(100L)).thenReturn(Optional.of(pendingTip));
        when(tipDAO.updateModeration(100L, ModerationStatus.APPROVED, null)).thenReturn(true);

        boolean success = moderationService.approveTip(1L, "admin", 100L);

        assertTrue(success);
        verify(tipDAO).updateModeration(100L, ModerationStatus.APPROVED, null);
        verify(asyncActivityProcessor).submitLog(any());
    }

    @Test
    @DisplayName("Reject tip requires a valid reason and transitions status to REJECTED")
    void testRejectTipWithReason() throws Exception {
        Tip pendingTip = new Tip();
        pendingTip.setId(101L);
        pendingTip.setTitle("Spam Promotion Link");
        pendingTip.setModerationStatus(ModerationStatus.PENDING);

        when(tipDAO.findById(101L)).thenReturn(Optional.of(pendingTip));
        when(tipDAO.updateModeration(eq(101L), eq(ModerationStatus.REJECTED), anyString())).thenReturn(true);

        boolean success = moderationService.rejectTip(1L, "admin", 101L, "Contains commercial advertising link.");

        assertTrue(success);
        verify(tipDAO).updateModeration(eq(101L), eq(ModerationStatus.REJECTED), anyString());
        verify(asyncActivityProcessor).submitLog(any());
    }

    @Test
    @DisplayName("Reject tip throws ValidationException when reason is empty or null")
    void testRejectTipWithoutReason() throws Exception {
        Tip pendingTip = new Tip();
        pendingTip.setId(102L);
        pendingTip.setTitle("Questionable Post");

        when(tipDAO.findById(102L)).thenReturn(Optional.of(pendingTip));

        assertThrows(ValidationException.class, () -> {
            moderationService.rejectTip(1L, "admin", 102L, "");
        }, "Empty rejection reason must throw ValidationException");

        assertThrows(ValidationException.class, () -> {
            moderationService.rejectTip(1L, "admin", 102L, "   ");
        }, "Whitespace rejection reason must throw ValidationException");

        assertThrows(ValidationException.class, () -> {
            moderationService.rejectTip(1L, "admin", 102L, null);
        }, "Null rejection reason must throw ValidationException");
    }

    @Test
    @DisplayName("Moderating non-existent content throws ResourceNotFoundException")
    void testModerationNotFound() throws Exception {
        when(tipDAO.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            moderationService.approveTip(1L, "admin", 999L);
        });
    }
}
